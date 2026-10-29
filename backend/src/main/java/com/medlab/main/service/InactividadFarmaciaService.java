package com.medlab.main.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.medlab.main.dto.AlertaInactividadDto;
import com.medlab.main.dto.RevisionInactividadDto;
import com.medlab.main.repository.InactividadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Revisión de inactividad (HU-9): alerta a las farmacias que llevan 30 días sin actualizar y
 * suspende la que acumula 3 alertas sin atender.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InactividadFarmaciaService {

  public static final int DIAS_SIN_ACTUALIZAR = 30;
  public static final int ALERTAS_PARA_SUSPENDER = 3;

  private final InactividadRepository inactividadRepository;
  private final TransactionTemplate transaccion;
  private final NotificadorAlertas notificador;

  /** Resultado de procesar una farmacia: cuántas alertas pendientes tiene y si se suspendió. */
  private record ResultadoFarmacia(long pendientes, boolean suspendida) {
  }

  /**
   * Cada farmacia va en su propia transacción: si una falla, las demás se procesan igual y la
   * suspensión nunca queda sin su auditoría (o al revés). El aviso a la farmacia sale cuando
   * su transacción ya terminó, así lo que ve al consultar coincide con lo que le avisamos.
   */
  public RevisionInactividadDto revisar() {
    LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    LocalDateTime limite = ahora.minusDays(DIAS_SIN_ACTUALIZAR);

    int alertas = 0;
    int suspendidas = 0;
    for (Integer farmaciaId : inactividadRepository.farmaciasParaAlertar(limite)) {
      try {
        ResultadoFarmacia resultado = transaccion.execute(estado -> procesar(farmaciaId, ahora));
        if (resultado == null) {
          continue;
        }
        alertas++;
        if (resultado.suspendida()) {
          suspendidas++;
        }
        notificador.alertaInactividad(new AlertaInactividadDto(farmaciaId, resultado.pendientes(),
            ALERTAS_PARA_SUSPENDER, resultado.suspendida(), ahora));
      } catch (RuntimeException e) {
        log.error("No se pudo revisar la inactividad de la farmacia {}", farmaciaId, e);
      }
    }
    return new RevisionInactividadDto(alertas, suspendidas);
  }

  /** Crea la alerta y, si ya son 3 sin atender, suspende. */
  private ResultadoFarmacia procesar(Integer farmaciaId, LocalDateTime ahora) {
    inactividadRepository.crearAlerta(farmaciaId, ahora);
    long pendientes = inactividadRepository.contarPendientes(farmaciaId);
    if (pendientes < ALERTAS_PARA_SUSPENDER) {
      return new ResultadoFarmacia(pendientes, false);
    }
    if (inactividadRepository.suspender(farmaciaId) == 0) {
      return new ResultadoFarmacia(pendientes, false);
    }
    inactividadRepository.registrarAuditoriaSuspension(farmaciaId, ahora);
    return new ResultadoFarmacia(pendientes, true);
  }
}