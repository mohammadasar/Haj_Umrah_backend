package com.example.myproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String FRONTEND_URL = "https://haj-umrah-services.netlify.app"; // ✅ Removed trailing slash
    private static final String UPLOADS_DIR = "/app/uploads"; // ✅ Ensure correct path

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ✅ CORS configuration for API endpoints
        registry.addMapping("/api/**")
                .allowedOrigins(FRONTEND_URL)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        // ✅ CORS configuration for serving files
        registry.addMapping("/uploads/**")
                .allowedOrigins(FRONTEND_URL)
                .allowedMethods("GET", "OPTIONS");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Ensure trailing slash in resource locations
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOADS_DIR + "/");
    }
}
