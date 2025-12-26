package com.example.scaffold.service;

import com.example.scaffold.mapper.AssetMapper;
import com.example.scaffold.mapper.AssetTrendMapper;
import com.example.scaffold.model.Asset;
import com.example.scaffold.model.AssetTrend;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 资产趋势数据自动生成器
 * 在Spring Boot启动完成后，自动为所有asset生成两年内的asset_trend数据
 */
@Slf4j
@Component
public class AssetTrendDataGenerator implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private AssetTrendMapper assetTrendMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private static final String[] INDUSTRIES = {"股票", "基金", "证券", "其他", "房地产", "商品"};
    private static final Map<String, Double> BASE_PRICES = new HashMap<>();
    private static final Map<String, Double> VOLATILITY = new HashMap<>();

    static {
        BASE_PRICES.put("股票", 200.0);
        BASE_PRICES.put("基金", 140.0);
        BASE_PRICES.put("证券", 200.0);
        BASE_PRICES.put("其他", 95.0);
        BASE_PRICES.put("房地产", 12500.0);
        BASE_PRICES.put("商品", 60.0);

        VOLATILITY.put("股票", 0.025);
        VOLATILITY.put("基金", 0.015);
        VOLATILITY.put("证券", 0.02);
        VOLATILITY.put("其他", 0.02);
        VOLATILITY.put("房地产", 0.008);
        VOLATILITY.put("商品", 0.03);
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 临时关闭SQL日志输出（因为配置使用的是StdOutImpl）
        PrintStream originalOut = System.out;
        // 创建一个过滤SQL输出的PrintStream包装器
        PrintStream filteredOut = new PrintStream(originalOut) {
            @Override
            public void println(String x) {
                // 过滤掉MyBatis SQL相关的输出
                if (x != null && (x.trim().startsWith("==>  Preparing:") 
                        || x.trim().startsWith("==> Parameters:") 
                        || x.trim().startsWith("<==    Updates:"))) {
                    return; // 不输出SQL日志
                }
                super.println(x); // 其他输出正常显示
            }
            
            @Override
            public void print(String s) {
                // 过滤掉MyBatis SQL相关的输出
                if (s != null && (s.trim().startsWith("==>  Preparing:") 
                        || s.trim().startsWith("==> Parameters:") 
                        || s.trim().startsWith("<==    Updates:"))) {
                    return; // 不输出SQL日志
                }
                super.print(s); // 其他输出正常显示
            }
        };
        System.setOut(filteredOut);
        
        try {
            log.info("开始自动生成asset_trend数据...");
            
            // 检查是否已有asset_trend数据
            long existingTrendCount = assetTrendMapper.selectCount(null);
            if (existingTrendCount > 0) {
                log.info("asset_trend表已有数据，跳过自动生成。现有记录数: {}", existingTrendCount);
                return;
            }

            // 查询所有asset记录
            List<Asset> assets = assetMapper.selectList(null);
            if (assets == null || assets.isEmpty()) {
                log.warn("未找到asset数据，跳过asset_trend数据生成");
                return;
            }

            log.info("找到{}条asset记录，开始生成asset_trend数据...", assets.size());

            // 生成日期范围：两年内的数据（从当前日期往前推两年）
            Calendar endDate = Calendar.getInstance();
            Calendar startDate = Calendar.getInstance();
            startDate.add(Calendar.YEAR, -2);

            List<String> allDates = generateDateRange(startDate, endDate);
            log.info("生成日期范围: {} 到 {}，共{}天", 
                formatDate(startDate.getTime()), 
                formatDate(endDate.getTime()), 
                allDates.size());

            Random random = new Random(42); // 使用固定种子保证可重复性
            int totalGenerated = 0;
            int batchSize = 1000; // 批量插入大小

            // 使用批量执行模式
            SqlSession batchSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            AssetTrendMapper batchMapper = batchSession.getMapper(AssetTrendMapper.class);
            
            try {
                // 收集所有需要插入的数据
                List<AssetTrend> allTrends = new ArrayList<>();
                
                // 为每个asset生成trend数据
                for (int assetIdx = 0; assetIdx < assets.size(); assetIdx++) {
                    Asset asset = assets.get(assetIdx);
                    List<AssetTrend> trends = generateTrendDataForAsset(asset, allDates, random);
                    allTrends.addAll(trends);
                    
                    // 每处理一定数量的asset，执行一次批量插入
                    if ((assetIdx + 1) % 10 == 0 || assetIdx == assets.size() - 1) {
                        // 批量插入当前批次
                        for (int i = 0; i < allTrends.size(); i += batchSize) {
                            int end = Math.min(i + batchSize, allTrends.size());
                            List<AssetTrend> batch = allTrends.subList(i, end);
                            for (AssetTrend trend : batch) {
                                batchMapper.insert(trend);
                            }
                        }
                        
                        // 提交当前批次
                        batchSession.commit();
                        batchSession.clearCache();
                        
                        totalGenerated += allTrends.size();
                        allTrends.clear();
                        
                        log.info("已处理 {}/{} 个asset，已生成 {} 条trend记录", 
                            assetIdx + 1, assets.size(), totalGenerated);
                    }
                }

                log.info("asset_trend数据生成完成！共生成{}条记录", totalGenerated);
            } finally {
                batchSession.close();
            }
        } catch (Exception e) {
            log.error("生成asset_trend数据时发生错误", e);
            // 不抛出异常，避免影响应用启动
        } finally {
            // 恢复SQL日志输出
            System.setOut(originalOut);
        }
    }

    /**
     * 为单个asset生成trend数据
     */
    private List<AssetTrend> generateTrendDataForAsset(Asset asset, List<String> allDates, Random random) {
        List<AssetTrend> trends = new ArrayList<>();
        
        if (asset.getId() == null || asset.getCurrentPrice() == null || asset.getRecordDate() == null) {
            return trends;
        }

        // 找到asset记录日期在allDates中的位置
        String assetDateStr = formatDate(asset.getRecordDate());
        int assetDateIdx = allDates.indexOf(assetDateStr);
        
        if (assetDateIdx == -1) {
            // 如果asset的recordDate不在范围内，使用中间位置
            assetDateIdx = allDates.size() / 2;
        }

        // 获取行业波动率
        String industry = asset.getIndustry() != null ? asset.getIndustry() : "其他";
        double vol = VOLATILITY.getOrDefault(industry, 0.02);
        
        // 以asset的currentPrice为基准价格
        double basePrice = asset.getCurrentPrice();
        
        // 生成价格序列
        double[] prices = new double[allDates.size()];
        prices[assetDateIdx] = basePrice; // 在asset记录日期那一天，价格必须与asset的currentPrice匹配
        
        // 向前生成价格（从asset记录日期向前到结束日期）
        double p = basePrice;
        for (int i = assetDateIdx + 1; i < allDates.size(); i++) {
            double change = random.nextGaussian() * vol + 0.0001 * Math.sin(i / 10.0);
            p = p * (1 + change);
            prices[i] = Math.max(0.01, p);
        }
        
        // 向后生成价格（从asset记录日期向后到开始日期）
        p = basePrice;
        for (int i = assetDateIdx - 1; i >= 0; i--) {
            double change = random.nextGaussian() * vol + 0.0001 * Math.sin(i / 10.0);
            p = p * (1 - change);
            prices[i] = Math.max(0.01, p);
        }
        
        // 确保在asset的recordDate那一天，价格与asset的currentPrice完全匹配
        prices[assetDateIdx] = basePrice;
        
        // 为每一天生成trend记录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < allDates.size(); i++) {
            try {
                Date date = sdf.parse(allDates.get(i));
                AssetTrend trend = new AssetTrend();
                trend.setAssetId(asset.getId());
                trend.setDate(date);
                trend.setPrice(prices[i]);
                trends.add(trend);
            } catch (Exception e) {
                log.warn("解析日期失败: {}", allDates.get(i), e);
            }
        }
        
        return trends;
    }

    /**
     * 生成日期范围列表
     */
    private List<String> generateDateRange(Calendar start, Calendar end) {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = (Calendar) start.clone();
        
        while (!cal.after(end)) {
            dates.add(sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return dates;
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}


