package com.example.scaffold.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "用户数据传输对象")
public class UserDTO {

    @Schema(description = "用户ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 10, message = "真实姓名长度必须在2-10个字符之间")
    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String realName;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @NotNull(message = "年龄不能为空")
    @Schema(description = "年龄", requiredMode = Schema.RequiredMode.REQUIRED, example = "25", minimum = "1", maximum = "150")
    private Integer age;

    @Schema(description = "性别: 0-未知 1-男 2-女", example = "1", allowableValues = {"0", "1", "2"})
    private Integer gender;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "用户状态: 0-禁用 1-启用", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "角色列表", example = "[\"admin\", \"user\"]")
    private List<String> roles;

    @Schema(description = "部门ID", example = "1001")
    private Long deptId;

    @Schema(description = "备注", example = "这是一个备注")
    private String remark;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private Date createTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private Date updateTime;
}