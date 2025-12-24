package com.example.scaffold.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录响应")
public class LoginResponse {
    @Schema(description = "是否成功", example = "true")
    private Boolean success;
    
    @Schema(description = "消息", example = "登录成功")
    private String message;
    
    @Schema(description = "Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "用户信息")
    private UserInfo user;
    
    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID", example = "1")
        private Long id;
        
        @Schema(description = "用户名", example = "admin")
        private String username;
        
        @Schema(description = "姓名", example = "管理员")
        private String name;
        
        @Schema(description = "角色", example = "admin")
        private String role;
    }
}

