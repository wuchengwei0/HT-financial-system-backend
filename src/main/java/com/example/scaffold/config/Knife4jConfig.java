package com.example.scaffold.config;

import com.example.scaffold.controller.ExportController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.List;

@Configuration
public class Knife4jConfig {

    @Bean
    public OperationCustomizer fileUploadCustomizer() {
        return (operation, handlerMethod) -> {
            if (isFileUploadMethod(handlerMethod)) {
                customizeFileUploadOperation(operation);
            }
            return operation;
        };
    }

    private boolean isFileUploadMethod(HandlerMethod handlerMethod) {
        return handlerMethod.getMethod().getName().equals("importFile")
                && handlerMethod.getBeanType().equals(ExportController.class);
    }

    private void customizeFileUploadOperation(Operation operation) {
        // 清除原有的参数和请求体
        if (operation.getParameters() != null) {
            operation.getParameters().clear();
        }
        operation.setRequestBody(null);

        // 创建文件上传请求体
        RequestBody requestBody = new RequestBody();
        requestBody.setRequired(true);
        requestBody.setDescription("上传文件（支持.sql/.xlsx/.xls格式）");

        Content content = new Content();
        MediaType mediaType = new MediaType();

        // 创建multipart/form-data的schema - 使用对象类型包含文件属性
        Schema<Object> multipartSchema = new Schema<>();
        multipartSchema.setType("object");
        multipartSchema.addRequiredItem("file");
        
        // 创建文件属性schema
        Schema<?> fileSchema = new Schema<>();
        fileSchema.setType("string");
        fileSchema.setFormat("binary");
        fileSchema.setDescription("上传文件（支持.sql/.xlsx/.xls格式）");
        
        // 将文件属性添加到multipart schema中
        multipartSchema.addProperty("file", fileSchema);
        
        mediaType.setSchema(multipartSchema);
        content.addMediaType("multipart/form-data", mediaType);
        requestBody.setContent(content);

        operation.setRequestBody(requestBody);
    }
}