package com.medlab.main.config;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de CORS de la API pública (capa de configuración).
 * <p>
 * Para qué sirve: el navegador bloquea las llamadas de un origen (la app Angular en {@code http://localhost:1420}, o la
 * app de escritorio Tauri) a otro (la API en {@code :8080}) salvo que la API lo permita de forma explícita. Esta clase
 * da ese permiso solo donde hace falta.
 * <p>
 * Cómo funciona: registra un bean {@link CorsConfigurationSource}, que {@link SecurityConfig} aplica con
 * {@code http.cors()}. Solo permite las rutas bajo {@link RutasApi#PUBLICA}, solo peticiones GET y solo desde los
 * orígenes de {@link CorsProperties}, que salen de la configuración en lugar de estar escritos en el código.
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

	/** Reglas de CORS del área pública: solo GET y solo desde los orígenes configurados */
	@Bean
	CorsConfigurationSource corsConfigurationSource(CorsProperties corsProperties) {
		CorsConfiguration publica = new CorsConfiguration();
		publica.setAllowedOrigins(corsProperties.origenesPermitidos());
		publica.setAllowedMethods(List.of("GET"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(RutasApi.PUBLICA + "/**", publica);
		return source;
	}
}
