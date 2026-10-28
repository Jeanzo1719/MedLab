package com.medlab.main.service;

import org.springframework.stereotype.Service;

import com.medlab.main.entity.Disponibilidad;
import com.medlab.main.exception.DisponibilidadNoEncontradaException;
import com.medlab.main.repository.CalificacionRepository;
import com.medlab.main.repository.DisponibilidadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalificacionService {

  private final DisponibilidadRepository disponibilidadRepository;
  private final CalificacionRepository calificacionRepository;
  private final MedicamentoDetalleNotificador medicamentoDetalleNotificador;

  public void registrar(Integer disponibilidadId, Integer usuarioId, Integer tipoCalificacionId) {
    Disponibilidad disponibilidad = disponibilidadRepository.findById(disponibilidadId)
        .orElseThrow(() -> new DisponibilidadNoEncontradaException(disponibilidadId));
    calificacionRepository.registrar(disponibilidadId, usuarioId, tipoCalificacionId);
    medicamentoDetalleNotificador.publicar(disponibilidad.getMedicamento().getId());
  }
}
