package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("distribution_historical")
@Schema(description = "历史配置实体")
public class DistributionHistorical {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "日期（YYYY-MM格式）", example = "2024-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String date;
    
    @Schema(description = "股票比例", example = "62.5")
    private Double stockPercentage;
    
    @Schema(description = "债券比例", example = "28.5")
    private Double bondPercentage;
    
    @Schema(description = "现金比例", example = "9.0")
    private Double cashPercentage;
}

