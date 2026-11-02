package com.medlab.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.medlab.main.security.SesionStompInterceptor;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class WebSocketSeguridadConfig implements WebSocketMessageBrokerConfigurer {

  private final SesionStompInterceptor sesionStompInterceptor;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(sesionStompInterceptor);
  }
}
