package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.service.impl.JwtServiceImpl;

/**
 * Pruebas del token de sesión: lo que de verdad importa es que el id y el rol viajen dentro, que
 * un token vencido no pase y que uno alterado tampoco.
 */
class JwtServiceImplTest {

  private static final String SECRETO = "clave-de-prueba-para-firmar-jwt-de-mas-de-32-bytes";
  private static final Instant INICIO = Instant.parse("2026-11-01T10:00:00Z");
  private static final Instant EXPIRACION = INICIO.plus(14, ChronoUnit.DAYS);

  private JwtServiceImpl jwtService;

  @BeforeEach
  void preparar() {
    jwtService = new JwtServiceImpl(SECRETO, "medlab");
  }

  @Test
  void elTokenLlevaElUsuarioYSuRol() {
    String token = jwtService.generarToken(42, 3, "farmacia", INICIO, EXPIRACION);

    assertNotNull(token);
    JwtService.TokenValidado datos = jwtService.validarToken(token);

    assertEquals(42, datos.usuarioId());
    assertEquals(3, datos.rolId());
    assertEquals("farmacia", datos.rol());
    assertEquals(EXPIRACION, datos.expiracion());
  }

  @Test
  void unTokenVencidoSeRechaza() {
    String token = jwtService.generarToken(42, 1, "paciente",
        INICIO.minus(2, ChronoUnit.DAYS), INICIO.minus(1, ChronoUnit.DAYS));

    assertThrows(SesionInvalidaException.class, () -> jwtService.validarToken(token));
  }

  @Test
  void unTokenAlteradoSeRechaza() {
    String token = jwtService.generarToken(42, 1, "paciente", INICIO, EXPIRACION);
    String alterado = token.substring(0, token.length() - 2) + (token.endsWith("a") ? "bb" : "aa");

    assertThrows(SesionInvalidaException.class, () -> jwtService.validarToken(alterado));
  }

  @Test
  void unTokenDeOtroEmisorSeRechaza() {
    JwtServiceImpl otro = new JwtServiceImpl(SECRETO, "otro-emisor");
    String token = otro.generarToken(42, 1, "paciente", INICIO, EXPIRACION);

    assertThrows(SesionInvalidaException.class, () -> jwtService.validarToken(token));
  }

  @Test
  void unTokenVacioSeRechaza() {
    assertThrows(SesionInvalidaException.class, () -> jwtService.validarToken(" "));
    assertThrows(SesionInvalidaException.class, () -> jwtService.validarToken(null));
  }
}
