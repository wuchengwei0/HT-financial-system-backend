package com.example.scaffold.config;

import com.example.scaffold.common.Result;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Configuration
public class SwaggerConfig {

    static {
        // 为Result类添加泛型支持
        SpringDocUtils.getConfig().replaceWithSchema(Result.class, new Schema<Result>()
                .addProperty("success", new Schema<Boolean>().type("boolean").example(true))
                .addProperty("code", new Schema<Integer>().type("integer").example(200))
                .addProperty("message", new Schema<String>().type("string").example("操作成功"))
                .addProperty("data", new Schema<Object>().type("object"))
                .addProperty("timestamp", new Schema<Long>().type("integer").example(1640995200000L)));

        // 注册Java 8时间类型的schema
        SpringDocUtils.getConfig().replaceWithSchema(LocalDateTime.class, new Schema<LocalDateTime>()
                .type("string").format("date-time").example("2024-01-01T10:00:00"));
        SpringDocUtils.getConfig().replaceWithSchema(LocalDate.class, new Schema<LocalDate>()
                .type("string").format("date").example("2024-01-01"));
        SpringDocUtils.getConfig().replaceWithSchema(Date.class, new Schema<Date>()
                .type("string").format("date-time").example("2024-01-01T10:00:00"));

        // 注册Map的schema
        SpringDocUtils.getConfig().replaceWithSchema(TreeMap.class, new Schema<Map<String, Object>>()
                .type("object").additionalProperties(new Schema<Object>()));
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HT-Financial-System API文档")
                        .description("HT-Financial-System API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发者")
                                .email("developer@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}