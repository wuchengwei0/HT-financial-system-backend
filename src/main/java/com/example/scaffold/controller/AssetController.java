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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assets")
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
    public Result<Map<String,Object>> getAssets(
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
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
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
        Asset asset = assetService.getById(id);
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

