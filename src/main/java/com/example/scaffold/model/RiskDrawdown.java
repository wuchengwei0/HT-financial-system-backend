package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@TableName("risk_drawdown")
@Schema(description = "回撤数据实体")
public class RiskDrawdown {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "日期", example = "2024-12-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    
    @Schema(description = "价格", example = "1250.50")
    private Double price;
    
    @Schema(description = "回撤百分比", example = "-5.2")
    private Double drawdown;
}

