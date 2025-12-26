package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.service.DashboardService;
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
@RequestMapping("/api/dashboard")
@Tag(name = "数据看板接口", description = "数据看板相关接口")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @Operation(
            summary = "获取仪表板核心指标",
            description = "获取数据看板的核心指标数据，支持按日期范围查询"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics(
            @Parameter(description = "开始日期（yyyy-MM-dd）", example = "2024-01-01")
            @RequestParam(required = false) String dateFrom,
            @Parameter(description = "结束日期（yyyy-MM-dd）", example = "2024-12-31")
            @RequestParam(required = false) String dateTo) {
        return Result.success(dashboardService.getMetrics(dateFrom, dateTo));
    }
    
    @Operation(
            summary = "获取资产分布数据",
            description = "获取资产分布数据，用于饼图展示，支持按日期范围查询"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/allocation")
    public Result<List<Map<String, Object>>> getAllocation(
            @Parameter(description = "开始日期（yyyy-MM-dd）", example = "2024-01-01")
            @RequestParam(required = false) String dateFrom,
            @Parameter(description = "结束日期（yyyy-MM-dd）", example = "2024-12-31")
            @RequestParam(required = false) String dateTo) {
        return Result.success(dashboardService.getAllocation(dateFrom, dateTo));
    }
    
    @Operation(
            summary = "获取业绩趋势数据",
            description = "获取业绩趋势数据，支持按日期范围查询"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/performance")
    public Result<List<Map<String, Object>>> getPerformance(
            @Parameter(description = "开始日期（yyyy-MM-dd）", example = "2024-01-01")
            @RequestParam(required = false) String dateFrom,
            @Parameter(description = "结束日期（yyyy-MM-dd）", example = "2024-12-31")
            @RequestParam(required = false) String dateTo) {
        return Result.success(dashboardService.getPerformance(dateFrom, dateTo));
    }
}

