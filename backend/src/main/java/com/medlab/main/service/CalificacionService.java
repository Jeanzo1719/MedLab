package com.medlab.main.service;

/**
 * 
 * el service de las calificaciones. Guarda lo que un usuario dice de un reporte
 * de disponibilidad ("Si hay", "Bajo pedido" o "No hay") y avisa el cambio
 * 
 * cada usuario tiene un solo voto por reporte: si vuelve a calificar, se cambia
 * su voto anterior. Después de guardar, el mensaje de disponibilidad del
 * medicamento se vuelve a calcular y se envía a todos los que lo están viendo
 * 
 */
public interface CalificacionService {

  /**
   * 
   * guarda la calificación de un usuario sobre un reporte y avisa el cambio
   * 
   * se hace en tres pasos.
   * a. Busca el reporte; si no existe, lanza DisponibilidadNotFoundException.
   * b. Guarda la calificación, o cambia la que el usuario ya tenía en ese reporte.
   * c. Le pide a MedicamentoDetalleNotificador que publique el detalle del
   * medicamento del reporte en /topic/medicamentos/{id}, con el mensaje nuevo
   * 
   * si el tipo de calificación no existe, la base de datos no la guarda y lanza
   * un error
   * 
   */
  void saveCalificacion(Integer disponibilidadId, Integer usuarioId, Integer tipoCalificacionId);
}
