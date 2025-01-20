package com.example.ecommerce.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;


@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(15));  // 개별 파일 최대 크기 15MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(20)); // 요청 전체 크기 20MB
        return factory.createMultipartConfig();
    }
}
