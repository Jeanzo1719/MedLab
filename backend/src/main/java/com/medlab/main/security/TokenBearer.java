package com.medlab.main.security;

/** Saca el token de un encabezado Authorization: Bearer ... */
public final class TokenBearer {

  private static final String PREFIJO = "Bearer ";

  private TokenBearer() {
  }

  /** Devuelve el token, o null si el encabezado no viene o no usa el esquema Bearer. */
  public static String extraer(String encabezado) {
    if (encabezado == null || !encabezado.startsWith(PREFIJO)) {
      return null;
    }
    String token = encabezado.substring(PREFIJO.length()).trim();
    return token.isEmpty() ? null : token;
  }
}
