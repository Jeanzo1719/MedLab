package com.medlab.main.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Deja que el frontend llame a la API REST desde su propio origen; sin esto el navegador
 * bloquea la llamada. Usa la misma lista de orígenes que el WebSocket, así hay un solo lugar
 * donde permitir o quitar uno. Solo cubre /api/** para no cambiar nada más.
 *
 * Desde HU-2 las llamadas autenticadas viajan con Authorization: Bearer; esa cabecera es la que
 * se permite aquí. SecurityConfig toma esta misma configuración para contestar la consulta
 * previa (OPTIONS) antes de exigir el token.
 */
@Configuration
public class PanelCorsConfig implements WebMvcConfigurer {

  @Value("${spring.cors.allowed-origins}")
  private List<String> allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // POST lo usa el panel del administrador para lanzar la revisión de inactividad.
    registry.addMapping("/api/**")
        .allowedOrigins(allowedOrigins.toArray(String[]::new))
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", "Authorization");
  }
}
