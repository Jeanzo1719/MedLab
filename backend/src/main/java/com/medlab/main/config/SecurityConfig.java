package com.medlab.main.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(Customizer.withDefaults()).csrf((csrf) -> csrf.disable())
        .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());
    return http.build();
  }

  // CORS de la API pública: solo GET y solo desde los orígenes de cors.origenes-permitidos (application-dev.yml)
  @Bean
  CorsConfigurationSource corsConfigurationSource(
      @Value("${cors.origenes-permitidos}") List<String> origenesPermitidos) {
    CorsConfiguration publica = new CorsConfiguration();
    publica.setAllowedOrigins(origenesPermitidos);
    publica.setAllowedMethods(List.of("GET"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/public/**", publica);
    return source;
  }
}
