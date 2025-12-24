package com.example.scaffold.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "import-xlsx")
public class ImportXlsxProperties {
    /** 路径如 classpath:init.xlsx 或 file:/xxx/init.xlsx */
    private String path;
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}

