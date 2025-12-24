package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("distribution_allocation")
@Schema(description = "资产配置实体")
public class DistributionAllocation {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "资产类别", example = "股票", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;
    
    @Schema(description = "配置比例", example = "45.5")
    @TableField("`value`")
    private Double value;
    
    @Schema(description = "颜色代码", example = "#1890ff")
    private String color;
    
    @Schema(description = "记录日期", example = "2024-01-01")
    @TableField("recordDate")
    private Date recordDate;
}

