package com.backend.ticketing_System.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up CORS (Cross-Origin Resource Sharing) in the application.
 */
@Configuration
public class CorsConfig {

    /**
     * Comma-separated list of frontend URLs allowed to access the backend.
     */
    @Value("${frontend.urls}")
    private String frontendUrls;

    /**
     * Bean definition for configuring CORS mappings.
     *
     * @return a WebMvcConfigurer instance with CORS mappings configured.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * Configure CORS mappings.
             *
             * @param registry the CorsRegistry to add mappings to.
             */
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontendUrls.split(","))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}