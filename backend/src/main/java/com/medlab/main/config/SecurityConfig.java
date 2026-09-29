package com.medlab.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security de la API (capa de configuración).
 * <p>
 * Cómo funciona: declara la {@link SecurityFilterChain} por la que pasa cada petición HTTP. Por ahora activa CORS con
 * las reglas de {@link CorsConfig}, desactiva CSRF (la API no guarda estado ni usa cookies) y deja pasar todas las
 * peticiones, porque la autenticación todavía no existe.
 * <p>
 * Para qué sirve: es el único lugar donde vivirán las reglas de acceso. Cuando llegue la historia de usuario de
 * autenticación, hay que restringir {@code permitAll()} para que solo {@link RutasApi#PUBLICA} quede abierta a los
 * visitantes.
 */
@Configuration
public class SecurityConfig {

	/** Cadena de filtros de cada petición: CORS activo, CSRF desactivado y, por ahora, todo permitido */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// cors() aplica las reglas del bean CorsConfigurationSource definido en CorsConfig
		http.cors(Customizer.withDefaults()).csrf((csrf) -> csrf.disable())
				.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll());
		return http.build();
	}
}
