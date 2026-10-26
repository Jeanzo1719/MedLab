package com.medlab.main.exception;

/** La farmacia no puede usar el panel (no existe o no está aprobada). Se responde 403. */
public class AccesoPanelDenegadoException extends RuntimeException {

  public AccesoPanelDenegadoException(String mensaje) {
    super(mensaje);
  }
}