package com.medlab.main.exception;

public class FarmaciaNotPendienteException extends RuntimeException {

  public FarmaciaNotPendienteException(Integer id, String estadoActual) {
    super("La farmacia con id " + id + " no está pendiente: su estado es " + estadoActual);
  }
}
