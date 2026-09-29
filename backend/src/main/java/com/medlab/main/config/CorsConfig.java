package com.medlab.main.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Lets the Angular frontend, served from another origin, call the public API. */
@Configuration
public class CorsConfig {

	/**
	 * Comma-separated origins allowed to call the public API. Locally they come from application-dev.yml; in Docker
	 * from the CORS_ORIGENES_PERMITIDOS environment variable.
	 */
	private final List<String> origenesPermitidos;

	public CorsConfig(@Value("${cors.origenes-permitidos}") List<String> origenesPermitidos) {
		this.origenesPermitidos = origenesPermitidos;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration publica = new CorsConfiguration();
		publica.setAllowedOrigins(origenesPermitidos);
		publica.setAllowedMethods(List.of("GET"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/public/**", publica);
		return source;
	}
}
