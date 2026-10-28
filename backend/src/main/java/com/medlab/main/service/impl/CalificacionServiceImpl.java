package com.medlab.main.service.impl;

import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.exception.DisponibilidadNotFoundException;
import com.medlab.main.repository.CalificacionesRepository;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.service.CalificacionService;
import com.medlab.main.service.MedicamentoDetalleNotificador;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Qué hace: guarda lo que un usuario dice de un reporte de disponibilidad y avisa el cambio a
 * quienes están viendo el medicamento.
 *
 * Cómo entenderlo: es como una encuesta de un solo voto por persona: si alguien vuelve a
 * votar, no se suma otro voto, se cambia el que ya tenía. Apenas se guarda, el letrero de
 * disponibilidad se recalcula y se muestra a todos.
 *
 * Detalles técnicos: lo hace en tres pasos. Primero busca el reporte en
 * DisponibilidadRepository; si no existe, lanza DisponibilidadNotFoundException. Después le
 * pide a CalificacionesRepository que guarde la calificación (o cambie la que el usuario ya
 * tenía, gracias a la restricción UNIQUE (disponibilidad_id, usuario_id)). Por último le pide
 * a MedicamentoDetalleNotificador que publique el detalle del medicamento del reporte en
 * /topic/medicamentos/{id}, con el mensaje de disponibilidad ya recalculado. Todavía no hay
 * controller que lo llame: se conectará con la HU de inicio de sesión, que es la que conoce el
 * usuario.
 */
@Service
@AllArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

  private final DisponibilidadRepository disponibilidadRepository;
  private final CalificacionesRepository calificacionesRepository;
  private final MedicamentoDetalleNotificador medicamentoDetalleNotificador;

  @Override
  public void saveCalificacion(Integer disponibilidadId, Integer usuarioId,
      Integer tipoCalificacionId) {
    DisponibilidadEntity disponibilidad = disponibilidadRepository.findById(disponibilidadId)
        .orElseThrow(() -> new DisponibilidadNotFoundException(disponibilidadId));
    calificacionesRepository.saveCalificacion(disponibilidadId, usuarioId, tipoCalificacionId);
    medicamentoDetalleNotificador
        .publishMedicamentoDetalleUpdate(disponibilidad.getMedicamento().getId());
  }
}
