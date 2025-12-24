package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.model.MonthlyReturn;
import com.example.scaffold.model.PortfolioTrend;
import com.example.scaffold.service.TrendService;
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
@RequestMapping("/trend")
@Tag(name = "趋势分析接口", description = "趋势分析相关接口")
public class TrendController {
    
    @Autowired
    private TrendService trendService;
    
    @Operation(
            summary = "获取投资组合趋势数据",
            description = "获取投资组合趋势数据，支持不同时间范围"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/portfolio")
    public Result<List<PortfolioTrend>> getPortfolioTrend(
            @Parameter(description = "时间范围", example = "1y")
            @RequestParam(defaultValue = "1y") String range) {
        return Result.success(trendService.getPortfolioTrend(range));
    }
    
    @Operation(
            summary = "获取月度收益数据",
            description = "获取月度收益数据，支持不同时间范围"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/monthly-returns")
    public Result<List<MonthlyReturn>> getMonthlyReturns(
            @Parameter(description = "时间范围", example = "1y")
            @RequestParam(defaultValue = "1y") String range) {
        return Result.success(trendService.getMonthlyReturns(range));
    }
    
    @Operation(
            summary = "获取趋势分析统计指标",
            description = "获取趋势分析的统计指标"
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
        return Result.success(trendService.getStats());
    }
}

