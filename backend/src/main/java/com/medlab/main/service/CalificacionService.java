package com.medlab.main.service;

/**
 * Qué hace: guarda lo que un usuario dice de un reporte de disponibilidad ("Si hay", "Bajo
 * pedido" o "No hay") y avisa el cambio a quienes están viendo el medicamento.
 *
 * Cómo entenderlo: es como una encuesta de un solo voto por persona: si alguien vuelve a
 * votar, no se suma otro voto, se cambia el que ya tenía. Apenas se guarda, el letrero de
 * disponibilidad se recalcula y se muestra a todos.
 */
public interface CalificacionService {

  /**
   * Guarda la calificación de un usuario sobre un reporte y avisa el cambio.
   *
   * disponibilidadId: id del reporte que se califica.
   * usuarioId: id del usuario que califica.
   * tipoCalificacionId: id del tipo elegido (1 "Si hay", 2 "Bajo pedido", 3 "No hay").
   *
   * Si el reporte no existe, lanza DisponibilidadNotFoundException. Si el tipo de
   * calificación no existe, la base de datos no la guarda y lanza
   * DataIntegrityViolationException.
   */
  void saveCalificacion(Integer disponibilidadId, Integer usuarioId, Integer tipoCalificacionId);
}
