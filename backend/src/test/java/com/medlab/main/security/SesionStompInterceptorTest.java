package com.medlab.main.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;

import com.medlab.main.entity.SesionesEntity;
import com.medlab.main.repository.SesionesRepository;
import com.medlab.main.service.JwtService;

/**
 * Pruebas de la puerta del WebSocket: los destinos públicos pasan sin sesión, los protegidos
 * exigen el rol correcto y cualquier destino sin regla se rechaza.
 */
@ExtendWith(MockitoExtension.class)
class SesionStompInterceptorTest {

  private static final String TOKEN = "token-jwt";

  @Mock
  private JwtService jwtService;
  @Mock
  private SesionesRepository sesionesRepository;

  private SesionStompInterceptor interceptor;

  @BeforeEach
  void preparar() {
    interceptor = new SesionStompInterceptor(jwtService, sesionesRepository);
  }

  @Test
  void destinosDeBusquedaYMapaPasanSinSesion() {
    assertNotNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/app/farmacias/aprobadas", null), null));
    assertNotNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/app/farmacias/cercanas", null), null));
    assertNotNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/app/medicamentos/buscar", null), null));
    assertNotNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/app/medicamentos/buscar/filtros", null), null));
    assertNotNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/app/medicamentos/detalle", null), null));
    assertNotNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/topic/medicamentos/7", null), null));
    verify(sesionesRepository, never()).findByToken(anyString());
  }

  @Test
  void destinoSinReglaSeRechazaAunqueHayaSesion() {
    assertNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/topic/nuevo-destino", TOKEN), null));
    assertNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/topic/medicamentos/7/extra", null), null));
  }

  @Test
  void enviarAUnDestinoPublicoSeRechaza() {
    assertNull(interceptor.preSend(mensaje(StompCommand.SEND, "/app/medicamentos/buscar", null), null));
  }

  @Test
  void panelPrivadoSinTokenSeRechaza() {
    assertNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/topic/farmacias/pendientes", null), null));
  }

  @Test
  void panelPrivadoConRolEquivocadoSeRechaza() {
    when(jwtService.validarToken(TOKEN))
        .thenReturn(new JwtService.TokenValidado(42, 3, "paciente", Instant.now().plusSeconds(600)));
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(sesion(LocalDateTime.now().plusDays(3))));

    assertNull(interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/topic/farmacias/pendientes", TOKEN), null));
  }

  @Test
  void panelPrivadoConRolCorrectoPasaYGuardaElUsuario() {
    when(jwtService.validarToken(TOKEN))
        .thenReturn(new JwtService.TokenValidado(1, 2, "administrador", Instant.now().plusSeconds(600)));
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(sesion(LocalDateTime.now().plusDays(3))));

    Message<?> resultado = interceptor.preSend(mensaje(StompCommand.SUBSCRIBE, "/topic/farmacias/pendientes", TOKEN), null);

    assertNotNull(resultado);
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(resultado);
    Authentication autenticacion = (Authentication) accessor.getUser();
    assertEquals("administrador", ((UsuarioAutenticado) autenticacion.getPrincipal()).rol());
  }

  @Test
  void calificarConSesionDeFarmaciaSeRechaza() {
    when(jwtService.validarToken(TOKEN))
        .thenReturn(new JwtService.TokenValidado(42, 1, "farmacia", Instant.now().plusSeconds(600)));
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(sesion(LocalDateTime.now().plusDays(3))));

    assertNull(interceptor.preSend(mensaje(StompCommand.SEND, "/app/calificaciones", TOKEN), null));
  }

  private static Message<byte[]> mensaje(StompCommand comando, String destino, String token) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(comando);
    accessor.setDestination(destino);
    if (token != null) {
      accessor.setNativeHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
    accessor.setLeaveMutable(true);
    return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
  }

  private static SesionesEntity sesion(LocalDateTime expiracion) {
    SesionesEntity sesion = new SesionesEntity();
    sesion.setId(99);
    sesion.setToken(TOKEN);
    sesion.setFechaExpiracion(expiracion);
    sesion.setActiva(true);
    return sesion;
  }
}
