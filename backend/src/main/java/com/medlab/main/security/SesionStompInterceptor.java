package com.medlab.main.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

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

/**
 * Qué hace: revisa los mensajes que llegan por WebSocket. Solo deja pasar los destinos públicos
 * (búsqueda, mapa y detalle) y los protegidos a quien tiene una sesión válida y el rol correcto.
 *
 * Cómo entenderlo: es el mismo portero de SesionJwtFilter, pero en la puerta del WebSocket.
 * La conexión sigue abierta para todos; lo que se revisa es cada mensaje, y un destino que no
 * esté en ninguna lista se rechaza.
 *
 * Detalles técnicos: mira los mensajes SEND y SUBSCRIBE. El token llega en el encabezado
 * Authorization de cada mensaje. Si el mensaje no cumple, se descarta y queda un aviso en el
 * log, sin cerrar la conexión. Si cumple, el usuario queda en el mensaje para que el controller
 * lo reciba como parámetro Authentication.
 */
@Slf4j
@Component
@AllArgsConstructor
public class SesionStompInterceptor implements ChannelInterceptor {

  private static final String ROL_ADMINISTRADOR = "administrador";
  private static final String ROL_FARMACIA = "farmacia";
  private static final String ROL_PACIENTE = "paciente";

  /** Los mensajes SEND solo pueden ir a los controllers, que empiezan por /app/. */
  private static final String PREFIJO_APLICACION = "/app/";

  /** Comodines y barras dobles: con ellos un destino como /topic/** escucharía los protegidos. */
  private static final List<String> SECUENCIAS_NO_PERMITIDAS = List.of("*", "?", "{", "}", "//");

  /** Las alertas de una farmacia, por ejemplo /topic/farmacias/7/alertas. */
  private static final Pattern DESTINO_ALERTAS_FARMACIA = Pattern.compile("^/topic/farmacias/\\d+/alertas$");

  /**
   * Los únicos destinos que cualquiera puede escuchar: las búsquedas, el mapa y el detalle del
   * medicamento. Todo lo que no esté aquí ni en ROLES_POR_DESTINO_PROTEGIDO se rechaza.
   */
  private static final List<Pattern> DESTINOS_PUBLICOS = List.of(
      Pattern.compile("^/app/farmacias/(aprobadas|cercanas)$"),
      Pattern.compile("^/app/medicamentos/(buscar|buscar/filtros|detalle)$"),
      Pattern.compile("^/topic/medicamentos/\\d+$"));

  /** Cada destino protegido con el único rol que puede usarlo. */
  private static final Map<String, String> ROLES_POR_DESTINO_PROTEGIDO = Map.of(
      "/app/farmacias/pendientes", ROL_ADMINISTRADOR,
      "/topic/farmacias/pendientes", ROL_ADMINISTRADOR,
      "/app/farmacias/aprobar", ROL_ADMINISTRADOR,
      "/app/farmacias/rechazar", ROL_ADMINISTRADOR,
      "/topic/admin/calificaciones", ROL_ADMINISTRADOR,
      "/app/calificaciones", ROL_PACIENTE);

  private final JwtService jwtService;
  private final SesionesRepository sesionesRepository;

  /**
   * Decide qué pasa con cada mensaje antes de que llegue a su destino: devuelve el mensaje para
   * dejarlo pasar o null para descartarlo.
   */
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

    String rolRequerido = findRolRequerido(destino);
    if (rolRequerido == null) {
      if (comando == StompCommand.SUBSCRIBE && esDestinoPublico(destino)) {
        return message;
      }
      log.warn("Destino STOMP sin regla de acceso: {} {}", comando, destino);
      return null;
    }

    try {
      UsuarioAutenticado usuario = authenticateUsuario(accessor);
      if (!isUsuarioAutorizado(usuario, rolRequerido, destino)) {
        log.warn("Usuario sin permiso para el destino STOMP: {} {}", comando, destino);
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

  /** Rechaza los destinos con comodines y los SEND que no van a un controller. */
  private boolean isDestinoPermitido(StompCommand comando, String destino) {
    if (destino == null || SECUENCIAS_NO_PERMITIDAS.stream().anyMatch(destino::contains)) {
      return false;
    }
    return comando != StompCommand.SEND || destino.startsWith(PREFIJO_APLICACION);
  }

  /** Devuelve el rol que exige el destino, o null si el destino es público. */
  private String findRolRequerido(String destino) {
    if (DESTINO_ALERTAS_FARMACIA.matcher(destino).matches()) {
      return ROL_FARMACIA;
    }
    return ROLES_POR_DESTINO_PROTEGIDO.get(destino);
  }

  private boolean esDestinoPublico(String destino) {
    return DESTINOS_PUBLICOS.stream().anyMatch(patron -> patron.matcher(destino).matches());
  }

  /** Revisa el rol y, en las alertas de una farmacia, que sean las de su propia cuenta. */
  private boolean isUsuarioAutorizado(UsuarioAutenticado usuario, String rolRequerido, String destino) {
    if (!rolRequerido.equals(usuario.rol())) {
      return false;
    }
    return !DESTINO_ALERTAS_FARMACIA.matcher(destino).matches()
        || destino.equals("/topic/farmacias/" + usuario.usuarioId() + "/alertas");
  }

  /**
   * Hace la misma revisión que SesionJwtFilter: verifica la firma del token, confirma en la tabla
   * sesiones que siga activa y sin vencer, y registra la actividad. Si algo falla, lanza
   * SesionInvalidaException.
   */
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
