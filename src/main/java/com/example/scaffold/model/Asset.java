package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("asset")
@Schema(description = "资产实体")
public class Asset {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "资产代码", example = "AAPL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;
    
    @Schema(description = "资产名称", example = "苹果公司", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    @Schema(description = "当前价格", example = "185.25")
    @TableField("currentPrice")
    private Double currentPrice;
    
    @Schema(description = "涨跌幅百分比", example = "2.35")
    @TableField("changePercent")
    private Double changePercent;
    
    @Schema(description = "市值", example = "2850000000")
    @TableField("marketValue")
    private Long marketValue;
    
    @Schema(description = "所属行业", example = "科技")
    @JsonProperty("assetCategory")  // TODO 123
    private String industry;
    
    @Schema(description = "持仓数量", example = "15000")
    private Integer position;
    
    @Schema(description = "成本价", example = "175.50")
    @TableField("costPrice")
    private Double costPrice;
    
    @Schema(description = "当日盈亏", example = "14625.00")
    @TableField("dailyGain")
    private Double dailyGain;
    
    @Schema(description = "总盈亏", example = "146250.00")
    @TableField("totalGain")
    private Double totalGain;
    
    @Schema(description = "持仓权重", example = "12.5")
    private Double weight;
    
    @Schema(description = "市盈率", example = "28.5")
    private Double pe;
    
    @Schema(description = "市净率", example = "8.2")
    private Double pb;
    
    @Schema(description = "股息率", example = "0.65")
    @TableField("dividendYield")
    private Double dividendYield;
    
    @Schema(description = "波动率", example = "0.85")
    private Double volatility;
    
    @Schema(description = "贝塔系数", example = "1.25")
    private Double beta;
    
    @Schema(description = "夏普比率", example = "1.85")
    @TableField("sharpeRatio")
    private Double sharpeRatio;
    
    @Schema(description = "最大回撤", example = "15.2")
    @TableField("maxDrawdown")
    private Double maxDrawdown;
    
    @Schema(description = "记录日期", example = "2024-01-01")
    @TableField("recordDate")
    private Date recordDate;
    
    @Schema(description = "资产趋势数据", example = "[]")
    @TableField(exist = false)
    private List<AssetTrend> tend;
}

