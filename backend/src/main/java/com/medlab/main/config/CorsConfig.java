package com.medlab.main.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Lets the Angular frontend, served from another origin, call the public API. */
@Configuration
public class CorsConfig {

  private static final List<String> ORIGENES_PERMITIDOS = List.of(
      // Angular dev server (ng serve, pnpm tauri dev)
      "http://localhost:1420",
      // Tauri app on Linux and macOS
      "tauri://localhost",
      // Tauri app on Windows and Android
      "http://tauri.localhost");

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration publica = new CorsConfiguration();
    publica.setAllowedOrigins(ORIGENES_PERMITIDOS);
    publica.setAllowedMethods(List.of("GET"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/public/**", publica);
    return source;
  }
}
