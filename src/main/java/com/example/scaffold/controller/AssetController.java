package com.example.scaffold.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scaffold.common.Result;
import com.example.scaffold.model.Asset;
import com.example.scaffold.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "资产列表接口", description = "资产列表相关接口")
public class AssetController {
    @Autowired
    private AssetService assetService;

    @Operation(
            summary = "获取资产列表",
            description = "获取资产分页列表，支持搜索、筛选和排序"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("")
    public Object getAssets(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词")
            @RequestParam(required = false) String search,
            @Parameter(description = "行业筛选")
            @RequestParam(required = false) String industry,
            @Parameter(description = "排序字段", example = "currentPrice")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(defaultValue = "desc") String sortOrder,
            @Parameter(description = "是否导出CSV", example = "false")
            @RequestParam(required = false, defaultValue = "false") Boolean export,
            HttpServletResponse response
    ) throws IOException {
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        if (search != null && !search.isEmpty()) {
            wrapper.like("code", search).or().like("name", search);
        }
        if (industry != null && !industry.isEmpty()) {
            wrapper.eq("industry", industry);
        }
        if (sortOrder.equalsIgnoreCase("desc")) {
            wrapper.orderByDesc(sortBy);
        } else {
            wrapper.orderByAsc(sortBy);
        }
        
        // 如果需要导出CSV
        if (Boolean.TRUE.equals(export)) {
            List<Asset> allAssets = assetService.list(wrapper);
            exportToCsv(allAssets, response);
            return null; // CSV导出直接返回文件流，不返回JSON
        }
        
        IPage<Asset> pageData = assetService.page(new Page<>(page, size), wrapper);
        // 统计
        List<Asset> allAssets = assetService.list(wrapper);
        Map<String,Object> stats = new HashMap<>();
        if (!allAssets.isEmpty()) {
            stats.put("totalMarketValue", allAssets.stream().mapToLong(a -> Optional.ofNullable(a.getMarketValue()).orElse(0L)).sum());
            stats.put("totalDailyGain", allAssets.stream().mapToDouble(a -> Optional.ofNullable(a.getDailyGain()).orElse(0.0)).sum());
            stats.put("avgChangePercent", allAssets.stream().mapToDouble(a -> Optional.ofNullable(a.getChangePercent()).orElse(0.0)).average().orElse(0));
            stats.put("count", allAssets.size());
        }
        Map<String,Object> pagination = new HashMap<>();
        pagination.put("page", pageData.getCurrent());
        pagination.put("size", pageData.getSize());
        pagination.put("total", pageData.getTotal());
        pagination.put("totalPages", pageData.getPages());
        Map<String,Object> result = new HashMap<>();
        result.put("data", pageData.getRecords());
        result.put("pagination", pagination);
        result.put("stats", stats);
        return Result.success(result);
    }
    
    /**
     * 导出资产列表为CSV文件
     * @param assets 资产列表
     * @param response HTTP响应
     */
    private void exportToCsv(List<Asset> assets, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode("assets_" + System.currentTimeMillis() + ".csv", StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        
        // 先写入UTF-8 BOM以支持Excel正确识别UTF-8编码
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);
        
        // 使用 OutputStreamWriter 并设置 UTF-8 编码
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // 写入CSV表头（排除tend字段）
//        writer.write("ID,资产代码,资产名称,当前价格,涨跌幅百分比,市值,所属行业,持仓数量,成本价,当日盈亏,总盈亏,持仓权重,市盈率,市净率,股息率,波动率,贝塔系数,夏普比率,最大回撤,记录日期\n");
        writer.write("资产代码,资产名称,当前价格,涨跌幅百分比,市值,所属行业,持仓数量,成本价,当日盈亏,总盈亏,持仓权重,市盈率,市净率,股息率,波动率,贝塔系数,夏普比率,最大回撤,记录日期\n");
        
        // 写入数据行
        for (Asset asset : assets) {
            String[] values = {
//                String.valueOf(asset.getId() != null ? asset.getId() : ""),
                escapeCsvValue(asset.getCode()),
                escapeCsvValue(asset.getName()),
                asset.getCurrentPrice() != null ? String.valueOf(asset.getCurrentPrice()) : "",
                asset.getChangePercent() != null ? String.valueOf(asset.getChangePercent()) : "",
                asset.getMarketValue() != null ? String.valueOf(asset.getMarketValue()) : "",
                escapeCsvValue(asset.getIndustry()),
                asset.getPosition() != null ? String.valueOf(asset.getPosition()) : "",
                asset.getCostPrice() != null ? String.valueOf(asset.getCostPrice()) : "",
                asset.getDailyGain() != null ? String.valueOf(asset.getDailyGain()) : "",
                asset.getTotalGain() != null ? String.valueOf(asset.getTotalGain()) : "",
                asset.getWeight() != null ? String.valueOf(asset.getWeight()) : "",
                asset.getPe() != null ? String.valueOf(asset.getPe()) : "",
                asset.getPb() != null ? String.valueOf(asset.getPb()) : "",
                asset.getDividendYield() != null ? String.valueOf(asset.getDividendYield()) : "",
                asset.getVolatility() != null ? String.valueOf(asset.getVolatility()) : "",
                asset.getBeta() != null ? String.valueOf(asset.getBeta()) : "",
                asset.getSharpeRatio() != null ? String.valueOf(asset.getSharpeRatio()) : "",
                asset.getMaxDrawdown() != null ? String.valueOf(asset.getMaxDrawdown()) : "",
                asset.getRecordDate() != null ? dateFormat.format(asset.getRecordDate()) : ""
            };
            writer.write(String.join(",", values) + "\n");
        }
        
        writer.flush();
        writer.close();
    }
    
    /**
     * 转义CSV值，处理包含逗号、引号或换行符的值
     */
    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        // 如果包含逗号、引号或换行符，需要用引号包围，并转义内部引号
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    @Operation(
            summary = "获取资产详情",
            description = "根据ID获取资产详细信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Asset.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "资产不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    public Result<Asset> getAssetDetail(
            @Parameter(description = "资产ID", example = "1", required = true)
            @PathVariable Long id) {
        Asset asset = assetService.getAssetDetailById(id);
        if (asset == null) {
            return Result.fail("资产不存在");
        }
        return Result.success(asset);
    }

    @Operation(
            summary = "获取行业筛选选项",
            description = "获取所有行业选项及其对应的资产数量"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/industries")
    public Result<List<Map<String, Object>>> getIndustries() {
        // 查询所有资产 分组聚合
        List<Asset> allAssets = assetService.list();
        Map<String, Long> industryCount = allAssets.stream().collect(
                Collectors.groupingBy(Asset::getIndustry, Collectors.counting()));
        List<Map<String,Object>> options = industryCount.entrySet().stream()
                .map(e -> {
                    Map<String, Object> opt = new HashMap<>();
                    opt.put("value", e.getKey());
                    opt.put("label", e.getKey());
                    opt.put("count", e.getValue());
                    return opt;
                }).collect(Collectors.toList());
        return Result.success(options);
    }

    /** 资产新增 **/
    @PostMapping("/save")
    public Result<Boolean> createAsset(@RequestBody Asset asset) {
        boolean saved = assetService.save(asset);
        if (saved) {
            return Result.success(true);
        }
        return Result.fail("新增资产失败");
    }
}

