package com.example.myproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String FRONTEND_URL = "https://haj-umrah-services.netlify.app/"; // 🔹 Replace with your frontend URL
    private static final String UPLOADS_DIR = "/app/uploads/"; // 🔹 Use relative path instead of "D:/"

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS configuration for API endpoints
        registry.addMapping("/api/**")
                .allowedOrigins(FRONTEND_URL) // 🔹 Allow only frontend, not "*"
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // 🔹 Allow credentials like cookies/tokens

        // CORS configuration for serving files
        registry.addMapping("/videos/**")
                .allowedOrigins(FRONTEND_URL)
                .allowedMethods("GET", "OPTIONS");

        registry.addMapping("/CardImage/**")
                .allowedOrigins(FRONTEND_URL)
                .allowedMethods("GET", "OPTIONS");

        registry.addMapping("/uploads/**")
                .allowedOrigins(FRONTEND_URL)
                .allowedMethods("GET", "OPTIONS");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 🔹 Use a relative path inside the container (Render does not support "D:/")
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + UPLOADS_DIR + "youtubeVideos/");

        registry.addResourceHandler("/CardImage/**")
                .addResourceLocations("file:" + UPLOADS_DIR + "CardImage/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + UPLOADS_DIR);
    }
}
