package com.medlab.main.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 
 * este archivo es la configuracion de websocket del backend, quien puede conectarse y como 
 * se reparten los mensajes
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * 
	 * la lista de direcciones desde las que se permite conectarse al WebSocket, 
	 * como la web en http://localhost:1420 y la app de escritorio de Tauri
	 * 
	 * {@code @Value} la lee de la propiedad cors.origenes-permitidos de application.yaml, 
	 * que a su vez la toma de la variable CORS_ORIGENES_PERMITIDOS del .env. La 
	 * lista separada por comas se convierte sola en una lista de Java
	 * 
	 */
	@Value("${cors.origenes-permitidos}")
	private List<String> origenesPermitidos;


	/**
	 * 
	 * abre la "puerta" del WebSocket en la dirección /ws. Es la que usa el frontend 
	 * para conectarse (ws://localhost:8080/ws)
	 * 
	 * setAllowedOrigins revisa desde qué página viene cada conexión y rechaza la que 
	 * no esté en la lista
	 *  
	 * Para entenderlo: esto evita que cualquier otra página web use nuestra API desde 
	 * el navegador de un usuario
	 * 
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins(origenesPermitidos.toArray(String[]::new));
	}

	/**
	 * 
	 * define que los mensajes cuyo destino empieza por /app van a los métodos de los controllers
	 * 
	 * cuando el frontend se suscribe a /app/farmacias/aprobadas, Spring le quita el /app y busca 
	 * el método marcado con "/farmacias/aprobadas"
	 * 
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
	}
}
