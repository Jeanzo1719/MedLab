package com.medlab.main.exception;

/**
 * La cuenta es de una farmacia que todavía no puede entrar: está pendiente de aprobación,
 * suspendida o rechazada. Cada estado tiene su mensaje para que el dueño sepa qué hacer.
 */
public class FarmaciaNoAprobadaException extends RuntimeException {

  private static final String ESTADO_PENDIENTE = "pendiente";
  private static final String ESTADO_SUSPENDIDA = "suspendida";
  private static final String ESTADO_RECHAZADA = "rechazada";

  public FarmaciaNoAprobadaException(String estado) {
    super(mensajePara(estado));
  }

  private static String mensajePara(String estado) {
    if (ESTADO_PENDIENTE.equals(estado)) {
      return "Tu farmacia está en espera de aprobación. Podrás iniciar sesión cuando un "
          + "administrador la apruebe.";
    }
    if (ESTADO_SUSPENDIDA.equals(estado)) {
      return "Tu farmacia está suspendida. Comunícate con el administrador de MedLab.";
    }
    if (ESTADO_RECHAZADA.equals(estado)) {
      return "La solicitud de tu farmacia fue rechazada.";
    }
    return "Tu farmacia todavía no está aprobada.";
  }
}
