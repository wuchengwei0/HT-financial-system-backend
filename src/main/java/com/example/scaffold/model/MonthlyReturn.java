package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("monthly_return")
@Schema(description = "月度收益实体")
public class MonthlyReturn {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "月份", example = "1月", requiredMode = Schema.RequiredMode.REQUIRED)
//    @TableField("`month`")
    @TableField(value = "`month`")
    private String month;
    
    @Schema(description = "收益率", example = "2.5")
    @TableField("returnRate")
    private Double returnRate;
    
    @Schema(description = "基准收益率", example = "1.8")
    @TableField("benchmarkRate")
    private Double benchmarkRate;
    
    @Schema(description = "超额收益", example = "0.7")
    @TableField("excessReturn")
    private Double excessReturn;
}

