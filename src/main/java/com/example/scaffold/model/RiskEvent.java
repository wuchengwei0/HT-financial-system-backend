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
@TableName("risk_event")
@Schema(description = "风险事件实体")
public class RiskEvent {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;
    
    @Schema(description = "事件日期", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date eventDate;
    
    @Schema(description = "风险类型", example = "市场风险", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    
    @Schema(description = "严重程度", example = "高", requiredMode = Schema.RequiredMode.REQUIRED)
    private String severity;
    
    @Schema(description = "事件描述", example = "股市大幅下跌")
    private String description;
    
    @Schema(description = "影响程度", example = "-5.2%")
    private String impact;
    
    @Schema(description = "状态", example = "已处理")
    private String status;
}

