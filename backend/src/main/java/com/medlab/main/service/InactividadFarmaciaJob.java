package com.medlab.main.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.medlab.main.dto.RevisionInactividadDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Dispara la revisión de inactividad una vez al día; la lógica vive en el service. */
@Slf4j
@Component
@RequiredArgsConstructor
public class InactividadFarmaciaJob {

  private final InactividadFarmaciaService inactividadService;

  /** Por defecto a las 3:00 a.m.; se puede cambiar con medlab.inactividad.cron. */
  @Scheduled(cron = "${medlab.inactividad.cron:0 0 3 * * *}")
  public void ejecutar() {
    // Una excepción sin atrapar no mata el scheduler, pero sí se perdería el log útil.
    try {
      RevisionInactividadDto resultado = inactividadService.revisar();
      log.info("Revisión de inactividad: {} alertas, {} farmacias suspendidas",
          resultado.alertasGeneradas(), resultado.farmaciasSuspendidas());
    } catch (RuntimeException e) {
      log.error("Falló la revisión de inactividad", e);
    }
  }
}