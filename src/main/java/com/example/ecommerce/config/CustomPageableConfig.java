package com.example.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class CustomPageableConfig implements PageableHandlerMethodArgumentResolverCustomizer {

    @Override
    public void customize(PageableHandlerMethodArgumentResolver resolver) {
        resolver.setOneIndexedParameters(true); // 페이지 번호를 1부터 시작
    }
}
