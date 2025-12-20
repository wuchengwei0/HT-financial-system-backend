package com.example.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class StarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarterApplication.class, args);
        System.out.println("==========================================");
        System.out.println("应用启动成功！");
        System.out.println("Knife4j文档地址: http://localhost:8080/api/doc.html");
        System.out.println("Swagger UI地址: http://localhost:8080/api/swagger-ui/index.html");
        System.out.println("健康检查地址: http://localhost:8080/api/test/health");
        System.out.println("==========================================");
    }
}