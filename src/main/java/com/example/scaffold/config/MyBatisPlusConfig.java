package com.example.scaffold.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 * 配置分页插件
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置 MyBatis Plus 拦截器
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型为 MySQL（H2 数据库配置了 MODE=MYSQL）
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置单页分页条数限制，默认无限制
        paginationInnerInterceptor.setMaxLimit(1000L);
        // 开启 count 的 join 优化,只针对部分 left join
//        paginationInnerInterceptor.setOptimizeJoinOfCountSql(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}

