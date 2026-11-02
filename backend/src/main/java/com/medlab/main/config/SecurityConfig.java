package com.medlab.main.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import tools.jackson.databind.ObjectMapper;
import com.medlab.main.dto.ErrorDto;
import com.medlab.main.repository.SesionesRepository;
import com.medlab.main.security.SesionJwtFilter;
import com.medlab.main.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Seguridad HTTP (HU-2).
 *
 * Las rutas públicas (registro, búsquedas, WebSocket y el propio inicio de sesión) siguen
 * abiertas. Los paneles exigen un token válido con el rol correcto; el resto de la API no cambia
 * en esta historia.
 *
 * La sesión no se guarda en el servidor: cada solicitud trae su JWT y SesionJwtFilter decide.
 * Por eso la política es STATELESS y no hay cookies de sesión de Spring.
 */
@Configuration
public class SecurityConfig {

  private static final String ROL_FARMACIA = "FARMACIA";
  private static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

  private final JwtService jwtService;
  private final SesionesRepository sesionesRepository;
  private final ObjectMapper objectMapper;

  public SecurityConfig(JwtService jwtService, SesionesRepository sesionesRepository,
      ObjectMapper objectMapper) {
    this.jwtService = jwtService;
    this.sesionesRepository = sesionesRepository;
    this.objectMapper = objectMapper;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf((csrf) -> csrf.disable())
        // Toma la configuración de orígenes de PanelCorsConfig (Spring MVC).
        .cors(Customizer.withDefaults())
        .sessionManagement((sesiones) -> sesiones
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests((autorizacion) -> autorizacion
            .requestMatchers(HttpMethod.POST, "/api/sesiones").permitAll()
            .requestMatchers("/api/panel-farmacia/**").hasRole(ROL_FARMACIA)
            .requestMatchers("/api/panel-admin/**").hasRole(ROL_ADMINISTRADOR)
            .requestMatchers("/api/sesiones/**").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/medicamentos/**").hasRole(ROL_ADMINISTRADOR)
            .requestMatchers(HttpMethod.PUT, "/api/medicamentos/**").hasRole(ROL_ADMINISTRADOR)
            .requestMatchers(HttpMethod.DELETE, "/api/medicamentos/**").hasRole(ROL_ADMINISTRADOR)
            .anyRequest().permitAll())
        .exceptionHandling((errores) -> errores
            .authenticationEntryPoint((solicitud, respuesta, excepcion) -> escribirError(respuesta,
                HttpServletResponse.SC_UNAUTHORIZED, "Inicia sesión para continuar"))
            .accessDeniedHandler((solicitud, respuesta, excepcion) -> escribirError(respuesta,
                HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para entrar a esta sección")))
        // Se construye aquí y no como bean para que Boot no lo registre dos veces.
        .addFilterBefore(new SesionJwtFilter(jwtService, sesionesRepository, objectMapper),
            UsernamePasswordAuthenticationFilter.class);

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

  private void escribirError(HttpServletResponse respuesta, int estado, String mensaje)
      throws IOException {
    respuesta.setStatus(estado);
    respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
    respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(respuesta.getWriter(), new ErrorDto(mensaje));
  }
}
