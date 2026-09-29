package com.medlab.main.config;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Lets the Angular frontend, served from another origin, call the public API. */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

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
