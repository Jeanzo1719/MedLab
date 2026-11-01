package com.medlab.main.exception;

/** Ya existe una cuenta con esa cédula. Se responde 409. */
public class IdentificacionDuplicadaException extends RuntimeException {

  public IdentificacionDuplicadaException(String identificacion) {
    super("Ya existe una cuenta con la identificación " + identificacion);
  }
}
