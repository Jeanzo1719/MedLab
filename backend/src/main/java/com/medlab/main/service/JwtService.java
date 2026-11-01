package com.medlab.main.service;

import java.time.Instant;

/**
 * Firma y valida los JWT de las sesiones (HU-2).
 *
 * El token lleva el id del usuario y su rol, que son los datos con los que el resto de la API
 * decide quién es y qué puede hacer. La firma es HMAC-SHA256 con una clave del servidor: si
 * alguien cambia el contenido, el token deja de valer.
 */
public interface JwtService {

  /** Datos que se leen de un token ya verificado. */
  record TokenValidado(Integer usuarioId, Integer rolId, String rol, Instant expiracion) {
  }

  /** Genera el JWT firmado para una sesión. */
  String generarToken(Integer usuarioId, Integer rolId, String rol, Instant inicio,
      Instant expiracion);

  /** Verifica firma y vencimiento y devuelve el contenido del token. */
  TokenValidado validarToken(String token);
}
