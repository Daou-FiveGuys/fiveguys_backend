package com.precapstone.fiveguys_backend.common.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Value("${frontend.server-domain}")
    private String serverDomain;

    @Value("${frontend.local-domain}")
    private String localDomain;

    @Value("${frontend.cloud-front-domain}")
    private String cloudFrontDomain;


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**")
                        // TODO 나중에 로컬은 지웁시다
                        .allowedOrigins(serverDomain, cloudFrontDomain, serverDomain+":80", serverDomain+":8080", localDomain+":3000", localDomain+":8080", cloudFrontDomain+":80", cloudFrontDomain+":8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                        .allowCredentials(true)
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization");

                registry.addMapping("/api-docs/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
