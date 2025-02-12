package com.example.myproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS configuration for API endpoints
        registry.addMapping("/api/**")
                .allowedOrigins("*") // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(false); // Set to false because "*" does not work with credentials

        // CORS configuration for video file requests
        registry.addMapping("/videos/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "OPTIONS");

        // CORS configuration for card images
        registry.addMapping("/CardImage/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "OPTIONS");
        
     // CORS configuration for uploads
        registry.addMapping("/uploads/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "OPTIONS");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serving video files
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:D:/my_own_projects/backend_projects/myproject/youtubeVideos/");

        // Serving card images
        registry.addResourceHandler("/CardImage/**")
        .addResourceLocations("file:D:/my_own_projects/backend_projects/myproject/CardImage/");
        
        // Serving uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/my_own_projects/backend_projects/myproject/uploads/");
    
    }
}
