package com.example.tsm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
            .components(new Components())
            .info(new Info()
                .title("Teacher-Student Management API")
                .version("v1")
                .description("师生管理系统 API 文档（MVP）"));
    }
}