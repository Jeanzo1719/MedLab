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
 * la configuracion de los WebSockets del backend: quien puede conectarse
 * (endpoints y origenes permitidos) y como se reparten los mensajes
 * (broker de mensajes y prefijos de destinos)
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 
     * los origenes permitidos para conectarse por WebSocket, definidos en la
     * propiedad spring.cors.allowed-origins (por ejemplo el servidor de
     * desarrollo de Angular y los origenes de Tauri)
     * 
     */
    @Value("${spring.cors.allowed-origins}")
    private List<String> allowedOrigins;

    /**
     * 
     * registra el endpoint /ws al que el frontend se conecta con un
     * WebSocket crudo (sin SockJS), permitiendo solo los origenes
     * configurados
     * 
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins.toArray(String[]::new));
    }

    /**
     * 
     * el broker simple atiende los destinos /ws (los broadcasts de
     * medicamentos del buscador) y /topic (los cambios del detalle de
     * medicamento); los mensajes que envia el frontend se procesan bajo el
     * prefijo /app
     * 
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/ws", "/topic");
    }
}
