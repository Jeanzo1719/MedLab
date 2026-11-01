package com.medlab.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf((csrf) -> csrf.disable()).authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());
    return http.build();
  }

  /**
   * Argon2 para las contraseñas (HU-1). Es el algoritmo que pide la historia: lento a propósito
   * y con sal propia por contraseña, para que una base robada no revele las claves. Los valores
   * son los recomendados por Spring Security desde su versión 5.8.
   */
  @Bean
  PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }
}
