package com.medlab.main.exception;

/**
 * El token no se puede leer, su sesión está cerrada o ya venció. Se responde 401 y el frontend
 * manda al usuario a iniciar sesión otra vez.
 */
public class SesionInvalidaException extends RuntimeException {

  public SesionInvalidaException() {
    super("La sesión expiró o no es válida. Inicia sesión de nuevo.");
  }
}
