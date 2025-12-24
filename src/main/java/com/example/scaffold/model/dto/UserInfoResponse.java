package com.example.scaffold.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "用户信息响应")
public class UserInfoResponse {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "姓名", example = "管理员")
    private String name;
    
    @Schema(description = "角色", example = "admin")
    private String role;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后登录时间", example = "2025-12-22T11:45:00")
    private Date lastLogin;
}

