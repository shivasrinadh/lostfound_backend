package com.campus.lostfound.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:3000,https://*.vercel.app}") String allowedOriginsProperty) {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOrigins = List.of(allowedOriginsProperty.split(","))
                .stream()
                .map(String::trim)
                .map(this::normalizeOrigin)
                .filter(origin -> !origin.isBlank())
                .toList();

        List<String> exactOrigins = new ArrayList<>();
        List<String> originPatterns = new ArrayList<>();

        for (String origin : allowedOrigins) {
            if (origin.contains("*")) {
                originPatterns.add(origin);
            } else {
                exactOrigins.add(origin);
            }
        }

        if (!exactOrigins.isEmpty()) {
            configuration.setAllowedOrigins(exactOrigins);
        }

        if (!originPatterns.isEmpty()) {
            configuration.setAllowedOriginPatterns(originPatterns);
        }

        if (exactOrigins.isEmpty() && originPatterns.isEmpty()) {
            configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        }

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private String normalizeOrigin(String origin) {
        String normalized = origin.trim();
        if (normalized.endsWith("/")) {
            return normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
