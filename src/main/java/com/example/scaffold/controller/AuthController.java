package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.model.dto.LoginRequest;
import com.example.scaffold.model.dto.LoginResponse;
import com.example.scaffold.model.dto.UserInfoResponse;
import com.example.scaffold.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "用户登录和认证相关接口")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Operation(
            summary = "用户登录",
            description = "用户登录接口，验证用户名和密码"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "登录成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "用户名或密码错误",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response.getSuccess()) {
            return response;
        } else {
            response.setSuccess(false);
            return response;
        }
    }
    
    @Operation(
            summary = "获取用户信息",
            description = "根据用户名获取用户详细信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "用户不存在",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/user")
    public Result<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        UserInfoResponse userInfo = authService.getUserInfo(authorization);
        if (userInfo != null) {
            return Result.success(userInfo);
        } else {
            return Result.fail("用户不存在");
        }
    }
}

