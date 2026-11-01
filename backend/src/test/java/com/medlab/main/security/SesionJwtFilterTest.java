package com.medlab.main.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medlab.main.entity.SesionesEntity;
import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.repository.SesionesRepository;
import com.medlab.main.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Pruebas del portero de cada solicitud: un token válido deja pasar y registra actividad; uno
 * alterado, vencido o de una sesión cerrada responde 401 y no llega al controller.
 */
@ExtendWith(MockitoExtension.class)
class SesionJwtFilterTest {

  private static final String TOKEN = "token-jwt";
  private static final Integer SESION_ID = 99;

  @Mock
  private JwtService jwtService;
  @Mock
  private SesionesRepository sesionesRepository;

  private SesionJwtFilter filtro;
  private MockHttpServletRequest solicitud;
  private MockHttpServletResponse respuesta;

  @BeforeEach
  void preparar() {
    filtro = new SesionJwtFilter(jwtService, sesionesRepository, new ObjectMapper());
    solicitud = new MockHttpServletRequest("GET", "/api/panel-farmacia/resumen");
    respuesta = new MockHttpServletResponse();
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void limpiar() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void unTokenValidoDejaPasarYRegistraActividad() throws Exception {
    solicitud.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
    when(jwtService.validarToken(TOKEN))
        .thenReturn(new JwtService.TokenValidado(42, 3, "farmacia", Instant.now().plusSeconds(600)));
    when(sesionesRepository.findByToken(TOKEN))
        .thenReturn(Optional.of(sesion(LocalDateTime.now().plusDays(3), true)));

    MockFilterChain cadena = new MockFilterChain();
    filtro.doFilter(solicitud, respuesta, cadena);

    assertNotNull(cadena.getRequest(), "La solicitud debe llegar al controller");
    verify(sesionesRepository).registrarActividad(SESION_ID);

    Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(autenticacion);
    assertTrue(autenticacion.getAuthorities().stream()
        .anyMatch((autoridad) -> "ROLE_FARMACIA".equals(autoridad.getAuthority())));
    assertEquals(42, ((UsuarioAutenticado) autenticacion.getPrincipal()).usuarioId());
  }

  @Test
  void unaSesionCerradaResponde401() throws Exception {
    solicitud.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
    when(jwtService.validarToken(TOKEN))
        .thenReturn(new JwtService.TokenValidado(42, 1, "paciente", Instant.now().plusSeconds(600)));
    when(sesionesRepository.findByToken(TOKEN))
        .thenReturn(Optional.of(sesion(LocalDateTime.now().plusDays(3), false)));

    filtro.doFilter(solicitud, respuesta, new MockFilterChain());

    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, respuesta.getStatus());
    verify(sesionesRepository, never()).registrarActividad(SESION_ID);
  }

  @Test
  void unaSesionVencidaResponde401() throws Exception {
    solicitud.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
    when(jwtService.validarToken(TOKEN))
        .thenReturn(new JwtService.TokenValidado(42, 1, "paciente", Instant.now().plusSeconds(600)));
    when(sesionesRepository.findByToken(TOKEN))
        .thenReturn(Optional.of(sesion(LocalDateTime.now().minusMinutes(1), true)));

    filtro.doFilter(solicitud, respuesta, new MockFilterChain());

    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, respuesta.getStatus());
  }

  @Test
  void unTokenQueNoSePuedeLeerResponde401() throws Exception {
    solicitud.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
    when(jwtService.validarToken(TOKEN)).thenThrow(new SesionInvalidaException());

    filtro.doFilter(solicitud, respuesta, new MockFilterChain());

    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, respuesta.getStatus());
    assertTrue(respuesta.getContentAsString().contains("sesión"));
  }

  @Test
  void sinTokenLaSolicitudSigueParaQueLasRutasPublicasFuncionen() throws Exception {
    MockFilterChain cadena = new MockFilterChain();
    filtro.doFilter(solicitud, respuesta, cadena);

    assertNotNull(cadena.getRequest());
    assertEquals(200, respuesta.getStatus());
  }

  private static SesionesEntity sesion(LocalDateTime expiracion, boolean activa) {
    SesionesEntity sesion = new SesionesEntity();
    sesion.setId(SESION_ID);
    sesion.setToken(TOKEN);
    sesion.setFechaExpiracion(expiracion);
    sesion.setActiva(activa);
    return sesion;
  }
}
