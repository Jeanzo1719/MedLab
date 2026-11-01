package com.medlab.main.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT firmados con HMAC-SHA256 (HU-2).
 *
 * Qué guarda: el id del usuario (sub), el id y el nombre de su rol, el emisor, la fecha de
 * emisión y la de vencimiento. Nada más: no lleva correo, teléfono ni contraseña.
 *
 * Cómo entenderlo: el token es como una pulsera firmada. El servidor sabe que la emitió él y
 * puede leer a quién pertenece, pero si alguien le cambia un dato la firma deja de cuadrar.
 */
@Service
public class JwtServiceImpl implements JwtService {

  private static final String CLAIM_ROL_ID = "rolId";
  private static final String CLAIM_ROL = "rol";

  private final SecretKey clave;
  private final String emisor;

  public JwtServiceImpl(@Value("${medlab.jwt.secret}") String secret,
      @Value("${medlab.jwt.emisor:medlab}") String emisor) {
    // Keys exige al menos 256 bits para HS256; si la clave es corta, la app no arranca.
    this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.emisor = emisor;
  }

  @Override
  public String generarToken(Integer usuarioId, Integer rolId, String rol, Instant inicio,
      Instant expiracion) {
    return Jwts.builder()
        .subject(String.valueOf(usuarioId))
        .claim(CLAIM_ROL_ID, rolId)
        .claim(CLAIM_ROL, rol)
        .issuer(emisor)
        .issuedAt(Date.from(inicio))
        .expiration(Date.from(expiracion))
        .signWith(clave, Jwts.SIG.HS256)
        .compact();
  }

  @Override
  public TokenValidado validarToken(String token) {
    if (token == null || token.isBlank()) {
      throw new SesionInvalidaException();
    }
    try {
      Claims contenido = Jwts.parser()
          .verifyWith(clave)
          .requireIssuer(emisor)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      Integer usuarioId = Integer.valueOf(contenido.getSubject());
      Integer rolId = contenido.get(CLAIM_ROL_ID, Integer.class);
      String rol = contenido.get(CLAIM_ROL, String.class);
      if (rolId == null || rol == null) {
        throw new SesionInvalidaException();
      }
      return new TokenValidado(usuarioId, rolId, rol, contenido.getExpiration().toInstant());
    } catch (JwtException | IllegalArgumentException e) {
      throw new SesionInvalidaException();
    }
  }
}
