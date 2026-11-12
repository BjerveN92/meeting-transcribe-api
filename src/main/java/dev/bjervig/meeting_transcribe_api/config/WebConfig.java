package dev.bjervig.meeting_transcribe_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(
                "tauri://localhost",         // Tauri v2 (Windows/Linux)
                "https://tauri.localhost",   // Tauri v2 (macOS)
                "http://localhost:1420",     // Vite dev server
                "http://localhost:5173"      // SvelteKit dev server
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
