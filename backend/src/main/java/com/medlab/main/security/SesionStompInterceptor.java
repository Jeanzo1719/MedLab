package com.medlab.main.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.medlab.main.entity.SesionesEntity;
import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.repository.SesionesRepository;
import com.medlab.main.service.JwtService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SesionStompInterceptor implements ChannelInterceptor {

  private static final String ROL_ADMINISTRADOR = "administrador";
  private static final String PREFIJO_APLICACION = "/app/";
  private static final List<String> SECUENCIAS_NO_PERMITIDAS = List.of("*", "?", "{", "}", "//");

  private static final Map<String, String> ROLES_POR_DESTINO_PROTEGIDO = Map.of(
      "/app/farmacias/pendientes", ROL_ADMINISTRADOR,
      "/topic/farmacias/pendientes", ROL_ADMINISTRADOR,
      "/app/farmacias/aprobar", ROL_ADMINISTRADOR,
      "/app/farmacias/rechazar", ROL_ADMINISTRADOR);

  private final JwtService jwtService;
  private final SesionesRepository sesionesRepository;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null) {
      return message;
    }

    StompCommand comando = accessor.getCommand();
    if (comando != StompCommand.SEND && comando != StompCommand.SUBSCRIBE) {
      return message;
    }

    String destino = accessor.getDestination();
    if (!isDestinoPermitido(comando, destino)) {
      log.warn("Destino STOMP rechazado: {} {}", comando, destino);
      return null;
    }

    String rolRequerido = ROLES_POR_DESTINO_PROTEGIDO.get(destino);
    if (rolRequerido == null) {
      return message;
    }

    try {
      UsuarioAutenticado usuario = authenticateUsuario(accessor);
      if (!rolRequerido.equals(usuario.rol())) {
        log.warn("Rol sin permiso para el destino STOMP: {} {}", comando, destino);
        return null;
      }
      SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority(
          "ROLE_" + usuario.rol().toUpperCase(Locale.ROOT));
      accessor.setUser(new UsernamePasswordAuthenticationToken(usuario, null, List.of(autoridad)));
      return message;
    } catch (SesionInvalidaException e) {
      log.warn("Sesión inválida para el destino STOMP: {} {}", comando, destino);
      return null;
    }
  }

  private boolean isDestinoPermitido(StompCommand comando, String destino) {
    if (destino == null || SECUENCIAS_NO_PERMITIDAS.stream().anyMatch(destino::contains)) {
      return false;
    }
    return comando != StompCommand.SEND || destino.startsWith(PREFIJO_APLICACION);
  }

  private UsuarioAutenticado authenticateUsuario(StompHeaderAccessor accessor) {
    String token = TokenBearer.extraer(accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION));
    JwtService.TokenValidado datos = jwtService.validarToken(token);

    SesionesEntity sesion = sesionesRepository.findByToken(token)
        .orElseThrow(SesionInvalidaException::new);
    if (!sesion.isActiva() || !sesion.getFechaExpiracion().isAfter(LocalDateTime.now())) {
      throw new SesionInvalidaException();
    }

    sesionesRepository.registrarActividad(sesion.getId());
    return new UsuarioAutenticado(datos.usuarioId(), datos.rolId(), datos.rol(), token);
  }
}
