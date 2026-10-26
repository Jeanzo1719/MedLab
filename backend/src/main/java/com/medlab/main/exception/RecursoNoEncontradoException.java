package com.medlab.main.exception;

/** El medicamento o el producto que se pide no existe. Se responde 404. */
public class RecursoNoEncontradoException extends RuntimeException {

  public RecursoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}