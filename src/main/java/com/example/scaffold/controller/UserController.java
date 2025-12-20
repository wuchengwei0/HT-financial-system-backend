package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.model.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@Tag(name = "用户管理", description = "用户相关的接口，包括用户CRUD、登录登出等")
@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    @Operation(summary = "创建用户", description = "创建新用户")
    @PostMapping
    public Result<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO,
                                     HttpServletRequest request) {
        return Result.success(userDTO);
    }

    @Operation(summary = "更新用户", description = "更新用户信息")
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable @Min(value = 1, message = "用户ID必须大于0") Long id,
                                     @Valid @RequestBody UserDTO userDTO,
                                     HttpServletRequest request) {

        return Result.success(userDTO);
    }
}