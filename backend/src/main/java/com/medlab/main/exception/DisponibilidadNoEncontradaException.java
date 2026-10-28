package com.medlab.main.exception;

public class DisponibilidadNoEncontradaException extends RuntimeException {

  public DisponibilidadNoEncontradaException(Integer id) {
    super("No existe el reporte de disponibilidad con id " + id);
  }
}
