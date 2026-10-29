package com.medlab.main.service.impl;

import com.medlab.main.entity.AuditoriaEstadosFarmaciaEntity;
import com.medlab.main.entity.EstadosFarmaciaEntity;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.exception.FarmaciaNotFoundException;
import com.medlab.main.exception.FarmaciaNotPendienteException;
import com.medlab.main.repository.AuditoriaEstadosFarmaciaRepository;
import com.medlab.main.repository.EstadosFarmaciaRepository;
import com.medlab.main.repository.FarmaciasRepository;
import com.medlab.main.service.AprobacionFarmaciasService;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AprobacionFarmaciasServiceImpl implements AprobacionFarmaciasService {

  private final FarmaciasRepository farmaciasRepository;
  private final EstadosFarmaciaRepository estadosFarmaciaRepository;
  private final AuditoriaEstadosFarmaciaRepository auditoriaEstadosFarmaciaRepository;

  @Override
  @Transactional
  public void approveFarmacia(Integer farmaciaId, Integer usuarioAdminId) {
    changeEstadoFarmacia(farmaciaId, usuarioAdminId, ESTADO_APROBADA);
  }

  @Override
  @Transactional
  public void rejectFarmacia(Integer farmaciaId, Integer usuarioAdminId) {
    changeEstadoFarmacia(farmaciaId, usuarioAdminId, ESTADO_RECHAZADA);
  }

  private void changeEstadoFarmacia(Integer farmaciaId, Integer usuarioAdminId,
      String nombreEstadoNuevo) {
    FarmaciasEntity farmacia = farmaciasRepository.findById(farmaciaId)
        .orElseThrow(() -> new FarmaciaNotFoundException(farmaciaId));
    EstadosFarmaciaEntity estadoAnterior = farmacia.getEstadoAprobacion();
    if (!ESTADO_PENDIENTE.equals(estadoAnterior.getNombre())) {
      throw new FarmaciaNotPendienteException(farmaciaId, estadoAnterior.getNombre());
    }
    EstadosFarmaciaEntity estadoNuevo = findEstadoFarmacia(nombreEstadoNuevo);

    farmacia.setEstadoAprobacion(estadoNuevo);
    farmacia.setFechaUltimaActualizacion(LocalDateTime.now());
    farmaciasRepository.save(farmacia);

    AuditoriaEstadosFarmaciaEntity auditoria = new AuditoriaEstadosFarmaciaEntity();
    auditoria.setFarmacia(farmacia);
    auditoria.setUsuarioAdminId(usuarioAdminId);
    auditoria.setEstadoAnterior(estadoAnterior);
    auditoria.setEstadoNuevo(estadoNuevo);
    auditoriaEstadosFarmaciaRepository.save(auditoria);
  }

  private EstadosFarmaciaEntity findEstadoFarmacia(String nombreEstado) {
    return estadosFarmaciaRepository.findByNombre(nombreEstado)
        .orElseThrow(() -> new IllegalStateException(
            "No existe el estado de farmacia " + nombreEstado));
  }
}
