package com.medlab.main.service.impl;

import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.exception.DisponibilidadNotFoundException;
import com.medlab.main.repository.CalificacionesRepository;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.service.CalificacionService;
import com.medlab.main.service.MedicamentoDetalleNotificador;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
