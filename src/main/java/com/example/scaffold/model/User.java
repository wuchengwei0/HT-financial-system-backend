package com.example.scaffold.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("\"user\"")
@Schema(description = "用户实体")
public class User {
    @TableId(type = IdType.AUTO)
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    
    @Schema(description = "密码", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;
    
    @Schema(description = "姓名", example = "管理员")
    private String name;
    
    @Schema(description = "角色", example = "admin")
    private String role;
    
    @Schema(description = "最后登录时间")
    private Date lastLogin;
}

