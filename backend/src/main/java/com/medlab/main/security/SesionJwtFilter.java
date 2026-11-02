package com.medlab.main.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import tools.jackson.databind.ObjectMapper;
import com.medlab.main.dto.ErrorDto;
import com.medlab.main.entity.SesionesEntity;
import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.repository.SesionesRepository;
import com.medlab.main.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Revisa el token de cada solicitud (HU-2).
 *
 * Qué hace: si viene Authorization: Bearer ..., verifica la firma del JWT, confirma en la tabla
 * sesiones que siga activa y sin vencer, registra la actividad y deja al usuario identificado
 * para el resto de la solicitud. Si el token está vencido, cerrado o alterado, responde 401 y no
 * deja pasar.
 *
 * Cómo entenderlo: es el portero que revisa la pulsera en cada puerta, no solo en la entrada.
 * Una pulsera anulada o vencida no abre nada, aunque se vea igual.
 */
public class SesionJwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final SesionesRepository sesionesRepository;
  private final ObjectMapper objectMapper;

  public SesionJwtFilter(JwtService jwtService, SesionesRepository sesionesRepository,
      ObjectMapper objectMapper) {
    this.jwtService = jwtService;
    this.sesionesRepository = sesionesRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // La consulta previa de CORS no lleva credenciales y no debe responder 401.
    return "OPTIONS".equalsIgnoreCase(request.getMethod());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = TokenBearer.extraer(request.getHeader(HttpHeaders.AUTHORIZATION));
    if (token == null) {
      // Sin token se sigue: las rutas públicas pasan y las privadas las corta SecurityConfig.
      filterChain.doFilter(request, response);
      return;
    }

    try {
      autenticar(token);
    } catch (SesionInvalidaException e) {
      SecurityContextHolder.clearContext();
      responderNoAutorizado(response, e.getMessage());
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void autenticar(String token) {
    JwtService.TokenValidado datos = jwtService.validarToken(token);

    SesionesEntity sesion = sesionesRepository.findByToken(token)
        .orElseThrow(SesionInvalidaException::new);
    if (!sesion.isActiva() || !sesion.getFechaExpiracion().isAfter(LocalDateTime.now())) {
      throw new SesionInvalidaException();
    }

    // Cada solicitud autenticada deja rastro: la base actualiza ultima_actividad sola.
    sesionesRepository.registrarActividad(sesion.getId());

    UsuarioAutenticado usuario = new UsuarioAutenticado(datos.usuarioId(), datos.rolId(),
        datos.rol(), token);
    SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority(
        "ROLE_" + datos.rol().toUpperCase(Locale.ROOT));
    UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
        usuario, null, List.of(autoridad));
    SecurityContextHolder.getContext().setAuthentication(autenticacion);
  }

  private void responderNoAutorizado(HttpServletResponse response, String mensaje) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), new ErrorDto(mensaje));
  }
}
