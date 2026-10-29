package com.medlab.main.exception;

/** Los datos violan una regla de negocio que las anotaciones del DTO no cubren. Se responde 400. */
public class DatosInvalidosException extends RuntimeException {

  public DatosInvalidosException(String mensaje) {
    super(mensaje);
  }
}