package com.medlab.main.exception;

/**
 * La identificación no existe o la contraseña no coincide. Se responde 401 con un mensaje
 * genérico a propósito: no se revela cuál de las dos falló.
 */
public class CredencialesInvalidasException extends RuntimeException {

  public CredencialesInvalidasException() {
    super("La identificación o la contraseña no son correctas");
  }
}
