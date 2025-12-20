package com.example.scaffold.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户查询条件")
public class UserQueryDTO {

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "状态: 0-禁用 1-启用", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "部门ID", example = "1001")
    private Long deptId;

    @Schema(description = "创建时间开始", example = "2024-01-01T00:00:00")
    private String createTimeStart;

    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59")
    private String createTimeEnd;
}