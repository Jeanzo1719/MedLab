package com.medlab.main.service;

import org.springframework.stereotype.Service;

import com.medlab.main.entity.Disponibilidad;
import com.medlab.main.exception.DisponibilidadNoEncontradaException;
import com.medlab.main.repository.CalificacionRepository;
import com.medlab.main.repository.DisponibilidadRepository;

import lombok.RequiredArgsConstructor;

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
@Service
@RequiredArgsConstructor
public class CalificacionService {

  /**
   * 
   * el repositorio que busca el reporte de disponibilidad que se califica
   * 
   */
  private final DisponibilidadRepository disponibilidadRepository;

  /**
   * 
   * el repositorio que guarda la calificación en la tabla calificaciones
   * 
   */
  private final CalificacionRepository calificacionRepository;

  /**
   * 
   * el que envía el mensaje de disponibilidad actualizado por WebSocket
   * 
   */
  private final MedicamentoDetalleNotificador medicamentoDetalleNotificador;

  /**
   * 
   * guarda la calificación de un usuario sobre un reporte y avisa el cambio
   * 
   * se hace en tres pasos.
   * a. Busca el reporte; si no existe, lanza DisponibilidadNoEncontradaException.
   * b. Guarda la calificación, o cambia la que el usuario ya tenía en ese reporte.
   * c. Le pide a MedicamentoDetalleNotificador que publique el detalle del
   * medicamento del reporte en /topic/medicamentos/{id}, con el mensaje nuevo
   * 
   * si el tipo de calificación no existe, la base de datos no la guarda y lanza
   * un error
   * 
   */
  public void registrar(Integer disponibilidadId, Integer usuarioId, Integer tipoCalificacionId) {
    Disponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId)
        .orElseThrow(() -> new DisponibilidadNoEncontradaException(disponibilidadId));
    calificacionRepository.registrar(disponibilidadId, usuarioId, tipoCalificacionId);
    medicamentoDetalleNotificador.publicar(disponibilidad.getMedicamento().getId());
  }
}
