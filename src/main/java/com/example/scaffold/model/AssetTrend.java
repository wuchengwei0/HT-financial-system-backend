package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@TableName("asset_trend")
@Schema(description = "资产趋势实体")
public class AssetTrend {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "资产ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableField("asset_id")
    private Long assetId;
    
    @Schema(description = "日期", example = "2024-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    
    @Schema(description = "价格", example = "185.25")
    private Double price;
}

