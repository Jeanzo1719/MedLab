package com.medlab.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.medlab.main.security.SesionStompInterceptor;

import lombok.AllArgsConstructor;

/**
 * Conecta SesionStompInterceptor con el WebSocket, para que revise cada mensaje que envía el
 * frontend. La configuración general del WebSocket sigue en WebSocketConfig.
 */
@Configuration
@AllArgsConstructor
public class WebSocketSeguridadConfig implements WebSocketMessageBrokerConfigurer {

  private final SesionStompInterceptor sesionStompInterceptor;

  /** Pone el interceptor en el canal por donde entran los mensajes del frontend. */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(sesionStompInterceptor);
  }
}
