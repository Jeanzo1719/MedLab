package com.medlab.main.exception;

/**
 * Qué hace: avisa que se intentó calificar un reporte de disponibilidad que no existe.
 *
 * Cómo entenderlo: es como intentar dejar una reseña de una tienda que no está en el
 * directorio.
 *
 * Detalles técnicos: la lanza CalificacionServiceImpl cuando DisponibilidadRepository no
 * encuentra el id del reporte. El mensaje incluye el id que se buscó.
 */
public class DisponibilidadNotFoundException extends RuntimeException {

  public DisponibilidadNotFoundException(Integer id) {
    super("No existe el reporte de disponibilidad con id " + id);
  }
}
