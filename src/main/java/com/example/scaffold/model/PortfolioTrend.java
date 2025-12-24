package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("portfolio_trend")
@Schema(description = "投资组合趋势实体")
public class PortfolioTrend {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "日期（YYYY-MM格式）", example = "2024-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String date;
    
    @Schema(description = "投资组合价值", example = "100.0")
    private Double portfolio;
    
    @Schema(description = "基准价值", example = "100.0")
    private Double benchmark;
    
    @Schema(description = "成交量", example = "1200000")
    private Long volume;
}

