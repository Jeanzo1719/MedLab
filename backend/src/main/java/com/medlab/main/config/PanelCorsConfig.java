package com.medlab.main.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.medlab.main.service.FarmaciaAutenticadaProvider;

/**
 * Deja que el frontend llame a la API REST del panel desde su propio origen; sin esto el
 * navegador bloquea la llamada. Usa la misma lista de orígenes que el WebSocket, así hay un
 * solo lugar donde permitir o quitar uno. Solo cubre /api/** para no cambiar nada más.
 *
 * Cuando el login (HU-2) cierre rutas en SecurityConfig habrá que activar también
 * http.cors(): Security responde antes que Spring MVC y rechazaría la consulta previa del
 * navegador (OPTIONS) si exige sesión.
 */
@Configuration
public class PanelCorsConfig implements WebMvcConfigurer {

  @Value("${spring.cors.allowed-origins}")
  private List<String> allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins(allowedOrigins.toArray(String[]::new))
        .allowedMethods("GET", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", FarmaciaAutenticadaProvider.ENCABEZADO);
  }
}