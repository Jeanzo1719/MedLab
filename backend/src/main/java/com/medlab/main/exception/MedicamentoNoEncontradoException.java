package com.medlab.main.exception;

/** Se lanza cuando se pide el detalle de un medicamento que no existe. */
public class MedicamentoNoEncontradoException extends RuntimeException {

  public MedicamentoNoEncontradoException(Integer id) {
    super("No existe el medicamento con id " + id);
  }
}