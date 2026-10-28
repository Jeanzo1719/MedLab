package com.medlab.main.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.DisponibilidadFarmaciaDto;
import com.medlab.main.dto.GuardarDisponibilidadDto;
import com.medlab.main.dto.GuardarDisponibilidadManualDto;

import lombok.RequiredArgsConstructor;

/**
 * Reporte manual de HU-7: encuentra o crea el medicamento y delega el guardado en
 * DisponibilidadService.guardar(), el mismo punto de escritura que usa HU-11.
 */
@Service
@RequiredArgsConstructor
public class DisponibilidadManualService {

  private final MedicamentoCatalogoService catalogoService;
  private final DisponibilidadService disponibilidadService;

  /**
   * Todo en una transacción: si guardar() falla (farmacia no aprobada, estado incoherente), el
   * medicamento recién creado se deshace y el catálogo no queda con filas sueltas.
   */
  @Transactional
  public DisponibilidadFarmaciaDto guardar(Integer farmaciaId, GuardarDisponibilidadManualDto datos) {
    Integer medicamentoId = catalogoService.buscarOCrear(datos);
    return disponibilidadService.guardar(farmaciaId,
        new GuardarDisponibilidadDto(medicamentoId, datos.cantidad(), datos.precio(), datos.estado()));
  }
}