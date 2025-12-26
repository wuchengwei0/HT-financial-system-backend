package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.model.RiskDrawdown;
import com.example.scaffold.model.RiskEvent;
import com.example.scaffold.service.RiskService;
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
@RequestMapping("/api/risk")
@Tag(name = "风险管理接口", description = "风险管理相关接口")
public class RiskController {
    
    @Autowired
    private RiskService riskService;
    
    @Operation(
            summary = "获取风险指标",
            description = "获取风险管理的各项指标数据"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics() {
        return Result.success(riskService.getMetrics());
    }
    
    @Operation(
            summary = "获取回撤数据",
            description = "获取回撤数据，支持指定天数"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/drawdown")
    public Result<List<RiskDrawdown>> getDrawdown(
            @Parameter(description = "天数", example = "365")
            @RequestParam(required = false) Integer days) {
        return Result.success(riskService.getDrawdown(days));
    }
    
    @Operation(
            summary = "获取风险事件",
            description = "获取风险事件列表"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/events")
    public Result<List<RiskEvent>> getEvents() {
        return Result.success(riskService.getEvents());
    }
}

