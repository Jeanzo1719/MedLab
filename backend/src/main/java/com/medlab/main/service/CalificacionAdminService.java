package com.medlab.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.medlab.main.entity.Calificacion;
import com.medlab.main.exception.RecursoNoEncontradoException;
import com.medlab.main.repository.CalificacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Eliminación de calificaciones falsas o abusivas por un administrador (HU-9). Borra solo la
 * fila de calificaciones: la disponibilidad queda intacta, porque la FK va de calificación a
 * disponibilidad y no al revés.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalificacionAdminService {

  private final CalificacionRepository calificacionRepository;
  private final MedicamentoDetalleNotificador notificador;

  @Transactional
  public void eliminar(Long calificacionId, Integer administradorId) {
    Calificacion calificacion = calificacionRepository.findById(calificacionId)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "La calificación " + calificacionId + " no existe"));
    Integer medicamentoId = calificacion.getDisponibilidad().getMedicamento().getId();

    calificacionRepository.delete(calificacion);
    log.info("Administrador {} eliminó la calificación {}", administradorId, calificacionId);
    publicarTrasCommit(medicamentoId);
  }

  /**
   * El mensaje de disponibilidad se calcula con las últimas calificaciones, así que al borrar
   * una puede cambiar. El aviso sale al terminar la transacción para que quien lo reciba ya
   * vea el dato nuevo; si falla, el borrado no se revierte.
   */
  private void publicarTrasCommit(Integer medicamentoId) {
    Runnable aviso = () -> {
      try {
        notificador.publicar(medicamentoId);
      } catch (RuntimeException e) {
        log.warn("No se pudo avisar el cambio del medicamento {}", medicamentoId, e);
      }
    };

    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          aviso.run();
        }
      });
    } else {
      aviso.run();
    }
  }
}