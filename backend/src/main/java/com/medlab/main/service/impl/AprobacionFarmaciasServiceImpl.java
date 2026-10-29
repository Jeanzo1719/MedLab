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

/**
 * Qué hace: aprueba o rechaza las farmacias que esperan la decisión del administrador y deja
 * registrado cada cambio.
 *
 * Cómo entenderlo: es como la ventanilla que revisa las solicitudes: firma "aprobada" o
 * "rechazada" y anota en la bitácora quién lo hizo y cuándo. Si la anotación falla, la firma
 * tampoco cuenta.
 *
 * Detalles técnicos: busca la farmacia en FarmaciasRepository y revisa que su estado sea
 * "pendiente". Busca el estado nuevo por su nombre en EstadosFarmaciaRepository y cambia el
 * estado_aprobacion_id de la farmacia (el id, nunca el nombre como texto), pone
 * fecha_ultima_actualizacion en la hora actual y guarda una fila en
 * auditoria_estados_farmacia con el estado anterior y el nuevo. Todo va en una sola
 * transacción (@Transactional): si algo falla, por ejemplo la auditoría, no se guarda nada.
 * Lo llama FarmaciasController con el administrador en null hasta que exista el inicio de
 * sesión.
 */
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

  /**
   * Hace el cambio de estado que comparten aprobar y rechazar.
   *
   * nombreEstadoNuevo: "aprobada" o "rechazada".
   */
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

  /** Busca un estado de farmacia por su nombre. Si no existe, la V1 está incompleta y lanza
   * IllegalStateException. */
  private EstadosFarmaciaEntity findEstadoFarmacia(String nombreEstado) {
    return estadosFarmaciaRepository.findByNombre(nombreEstado)
        .orElseThrow(() -> new IllegalStateException(
            "No existe el estado de farmacia " + nombreEstado));
  }
}
