package com.example.scaffold.controller;

import com.example.scaffold.common.Result;
import com.example.scaffold.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Tag(name = "测试管理", description = "测试接口管理")
@Validated
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务运行正常");
    }

    @Operation(summary = "测试成功响应", description = "测试接口成功返回")
    @GetMapping("/success")
    public Result<String> testSuccess() {
        return Result.success("请求成功");
    }

    @Operation(summary = "测试业务异常", description = "测试业务异常处理机制")
    @GetMapping("/exception")
    public Result<String> testException() {
        throw new BusinessException("测试业务异常");
    }

    @Operation(summary = "测试参数校验", description = "测试参数校验功能")
    @GetMapping("/validate")
    public Result<String> testValidate(
            @Parameter(description = "测试参数", required = true, example = "hello")
            @RequestParam @NotBlank(message = "参数不能为空") String param) {
        return Result.success("参数: " + param);
    }

    @Operation(summary = "测试系统异常", description = "测试系统异常处理机制")
    @GetMapping("/system-error")
    public Result<String> testSystemError() {
        int i = 1 / 0; // 模拟除零异常
        return Result.success("永远不会执行到这里");
    }

    @Operation(summary = "POST请求测试", description = "测试POST请求")
    @PostMapping("/post-test")
    public Result<String> postTest(@Valid @RequestBody TestRequest request) {
        return Result.success("收到请求: " + request.getName());
    }

    @Data
    public static class TestRequest {
        @NotBlank(message = "姓名不能为空")
        private String name;

        @NotNull(message = "年龄不能为空")
        private Integer age;
    }
}