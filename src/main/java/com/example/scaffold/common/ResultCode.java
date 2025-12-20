package com.example.scaffold.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "响应码枚举")
public enum ResultCode {
    @Schema(description = "成功")
    SUCCESS(200, "操作成功"),

    @Schema(description = "失败")
    FAIL(500, "操作失败"),

    @Schema(description = "未授权")
    UNAUTHORIZED(401, "未授权"),

    @Schema(description = "禁止访问")
    FORBIDDEN(403, "禁止访问"),

    @Schema(description = "资源不存在")
    NOT_FOUND(404, "资源不存在"),

    @Schema(description = "请求参数错误")
    BAD_REQUEST(400, "请求参数错误");

    @Schema(description = "响应码", example = "200")
    private final Integer code;

    @Schema(description = "响应消息", example = "操作成功")
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取枚举
     */
    public static ResultCode getByCode(Integer code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return FAIL;
    }
}