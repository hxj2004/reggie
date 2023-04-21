package com.example.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//mp配置类
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){

//        定义mp拦截器
        MybatisPlusInterceptor mpinterceptor = new MybatisPlusInterceptor();
//        添加具体拦截器
        mpinterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return  mpinterceptor;
    }
}
