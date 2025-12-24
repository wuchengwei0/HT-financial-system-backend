package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.scaffold.mapper.AssetMapper;
import com.example.scaffold.mapper.DistributionAllocationMapper;
import com.example.scaffold.mapper.PortfolioTrendMapper;
import com.example.scaffold.model.Asset;
import com.example.scaffold.model.DistributionAllocation;
import com.example.scaffold.model.PortfolioTrend;
import com.example.scaffold.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private DistributionAllocationMapper distributionAllocationMapper;
    
    @Autowired
    private PortfolioTrendMapper portfolioTrendMapper;

    @Override
    public Map<String, Object> getMetrics(String dateFrom, String dateTo) {
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        
        // 如果提供了日期范围，添加日期过滤条件
        if (StringUtils.hasText(dateFrom) && StringUtils.hasText(dateTo)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date from = sdf.parse(dateFrom);
                Date to = sdf.parse(dateTo);
                wrapper.between("recordDate", from, to);
            } catch (Exception e) {
                throw new RuntimeException("日期格式错误", e);
            }
        }
        
        List<Asset> assets = assetMapper.selectList(wrapper);

        double totalAssets = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getCurrentPrice()).orElse(0.0) *
                        Optional.ofNullable(a.getPosition()).orElse(0))
                .sum();

        double dailyPnL = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getDailyGain()).orElse(0.0))
                .sum();

        double totalPnL = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getTotalGain()).orElse(0.0))
                .sum();

        double avgSharpeRatio = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getSharpeRatio()).orElse(0.0))
                .average()
                .orElse(0.0);

        double avgMaxDrawdown = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getMaxDrawdown()).orElse(0.0))
                .average()
                .orElse(0.0);

        double avgVolatility = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getVolatility()).orElse(0.0))
                .average()
                .orElse(0.0);

        double avgBeta = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getBeta()).orElse(0.0))
                .average()
                .orElse(0.0);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAssets", totalAssets);
        metrics.put("dailyPnL", dailyPnL);
        metrics.put("totalPnL", totalPnL);
        metrics.put("sharpeRatio", Math.round(avgSharpeRatio * 100.0) / 100.0);
        metrics.put("maxDrawdown", Math.round(avgMaxDrawdown * 100.0) / 100.0);
        metrics.put("volatility", Math.round(avgVolatility * 100.0) / 100.0);
        metrics.put("beta", Math.round(avgBeta * 100.0) / 100.0);
        metrics.put("alpha", 0.05);
        metrics.put("winRate", 65.5);
        metrics.put("avgReturn", 1.25);

        return metrics;
    }

    @Override
    public List<Map<String, Object>> getAllocation(String dateFrom, String dateTo) {
        QueryWrapper<DistributionAllocation> wrapper = new QueryWrapper<>();
        
        // 如果提供了日期范围，添加日期过滤条件
        if (StringUtils.hasText(dateFrom) && StringUtils.hasText(dateTo)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date from = sdf.parse(dateFrom);
                Date to = sdf.parse(dateTo);
                wrapper.between("recordDate", from, to);
            } catch (Exception e) {
                throw new RuntimeException("日期格式错误", e);
            }
        }
        
        // 从 distribution_allocation 表读取数据
        List<DistributionAllocation> list = distributionAllocationMapper.selectList(wrapper);

        return list.stream()
                .map(da -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("category", da.getCategory());
                    item.put("value", da.getValue());
                    item.put("color", da.getColor());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getPerformance(String dateFrom, String dateTo) {
        QueryWrapper<PortfolioTrend> wrapper = new QueryWrapper<>();
        
        // 如果提供了日期范围，添加日期过滤条件
        if (StringUtils.hasText(dateFrom) && StringUtils.hasText(dateTo)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date from = sdf.parse(dateFrom);
                Date to = sdf.parse(dateTo);
                wrapper.between("recordDate", from, to);
            } catch (Exception e) {
                throw new RuntimeException("日期格式错误", e);
            }
        }
        
        wrapper.orderByAsc("recordDate");
        
        List<PortfolioTrend> trends = portfolioTrendMapper.selectList(wrapper);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return trends.stream()
                .map(trend -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", sdf.format(trend.getRecordDate()));
                    item.put("portfolio", trend.getPortfolio());
                    item.put("benchmark", trend.getBenchmark());
                    if (trend.getVolume() != null) {
                        item.put("volume", trend.getVolume());
                    }
                    return item;
                })
                .collect(Collectors.toList());
    }
}

