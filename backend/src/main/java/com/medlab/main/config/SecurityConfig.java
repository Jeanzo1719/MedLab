package com.medlab.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // cors() applies the rules from the CorsConfigurationSource bean in CorsConfig
    http.cors(Customizer.withDefaults())
        .csrf((csrf) -> csrf.disable())
        .authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());
    return http.build();
  }
}
