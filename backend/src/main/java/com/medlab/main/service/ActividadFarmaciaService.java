package com.medlab.main.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.repository.InactividadRepository;

import lombok.RequiredArgsConstructor;

/**
 * Registra que una farmacia está activa (HU-9). Lo llaman HU-11 y HU-7 al guardar datos o
 * inventario; así hay un solo lugar que mueve la fecha y atiende las alertas de inactividad.
 * Se une a la transacción del que llama: si el guardado se deshace, esto también.
 */
@Service
@RequiredArgsConstructor
public class ActividadFarmaciaService {

  private final InactividadRepository inactividadRepository;

  /** Cambio de inventario: mueve la fecha de la farmacia y atiende sus alertas. */
  @Transactional
  public void registrarActividad(Integer farmaciaId) {
    LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    inactividadRepository.moverFechaActualizacion(farmaciaId, ahora);
    inactividadRepository.marcarAlertasAtendidas(farmaciaId);
  }

  /** Edición de datos: FarmaciaPanelService ya movió la fecha, solo faltan las alertas. */
  @Transactional
  public void atenderAlertas(Integer farmaciaId) {
    inactividadRepository.marcarAlertasAtendidas(farmaciaId);
  }
}