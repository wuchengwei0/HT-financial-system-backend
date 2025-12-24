package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.model.DistributionAllocation;
import com.example.scaffold.model.DistributionHistorical;
import com.example.scaffold.service.DistributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/distribution")
@Tag(name = "资产分布接口", description = "资产分布相关接口")
public class DistributionController {
    
    @Autowired
    private DistributionService distributionService;
    
    @Operation(
            summary = "获取资产配置数据",
            description = "获取资产配置数据，支持分类和时间范围筛选"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/allocation")
    public Result<List<DistributionAllocation>> getAllocation(
            @Parameter(description = "资产类别", example = "all")
            @RequestParam(defaultValue = "all") String category,
            @Parameter(description = "时间范围", example = "current")
            @RequestParam(defaultValue = "current") String range) {
        return Result.success(distributionService.getAllocation(category, range));
    }
    
    @Operation(
            summary = "获取历史配置变化数据",
            description = "获取历史配置变化数据"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/historical")
    public Result<List<DistributionHistorical>> getHistorical() {
        return Result.success(distributionService.getHistorical());
    }
    
    @Operation(
            summary = "获取资产分布统计指标",
            description = "获取资产分布的统计指标"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(distributionService.getStats());
    }
}

