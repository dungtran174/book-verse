package com.bookverse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình Web MVC.
 * - CORS: cho phép frontend gọi API từ domain khác.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Cho phép CORS từ tất cả origin trong môi trường dev.
     * Trong production nên giới hạn origin cụ thể.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
