package com.precapstone.fiveguys_backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        /**
         * 3000에서 오는 요청 허용
         */
        registry.addMapping("/**").allowedOrigins("http://localhost:3000");
    }
}
