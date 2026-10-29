package com.medlab.main.exception;

/**
 * Qué hace: avisa que se intentó aprobar o rechazar una farmacia que no está pendiente.
 *
 * Cómo entenderlo: es como querer aprobar una solicitud que ya fue aprobada o rechazada: la
 * decisión ya se tomó y no se vuelve a tomar.
 *
 * Detalles técnicos: la lanza AprobacionFarmaciasServiceImpl antes de cambiar nada, así no
 * se guarda ningún cambio ni ninguna auditoría. FarmaciasController la captura y deja un
 * aviso en el log del backend; el cliente no recibe respuesta. El mensaje incluye el id y el
 * estado que tiene la farmacia.
 */
public class FarmaciaNotPendienteException extends RuntimeException {

  public FarmaciaNotPendienteException(Integer id, String estadoActual) {
    super("La farmacia con id " + id + " no está pendiente: su estado es " + estadoActual);
  }
}
