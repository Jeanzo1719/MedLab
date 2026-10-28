package com.medlab.main.exception;

/**
 * 
 * avisa que se intentó calificar un reporte de disponibilidad que no existe
 * 
 * la lanza CalificacionService cuando DisponibilidadRepository no encuentra el
 * id del reporte. El mensaje incluye el id que se buscó
 * 
 */
public class DisponibilidadNoEncontradaException extends RuntimeException {

  public DisponibilidadNoEncontradaException(Integer id) {
    super("No existe el reporte de disponibilidad con id " + id);
  }
}
