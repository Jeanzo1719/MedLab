package com.medlab.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.DatosFarmaciaDto;
import com.medlab.main.dto.DisponibilidadFarmaciaDto;
import com.medlab.main.dto.GuardarDatosFarmaciaDto;
import com.medlab.main.dto.GuardarDisponibilidadDto;
import com.medlab.main.dto.PaginaDto;
import com.medlab.main.dto.ResumenInventarioDto;
import com.medlab.main.service.DisponibilidadService;
import com.medlab.main.service.FarmaciaAutenticadaProvider;
import com.medlab.main.service.FarmaciaPanelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Panel administrativo de la farmacia (HU-11). Es REST y no STOMP porque son operaciones de
 * escritura de una sola farmacia; STOMP queda para las pantallas públicas que se actualizan
 * en vivo. Sin lógica propia: la farmacia sale del provider y todo lo decide el service.
 * Guardar es un PUT porque sirve igual para crear y para editar (upsert).
 */
@RestController
@RequestMapping("/api/panel-farmacia")
@RequiredArgsConstructor
public class PanelFarmaciaController {

  private final DisponibilidadService disponibilidadService;
  private final FarmaciaPanelService farmaciaPanelService;
  private final FarmaciaAutenticadaProvider farmaciaAutenticada;

  @GetMapping("/inventario")
  public PaginaDto<DisponibilidadFarmaciaDto> listar(
      @RequestParam(defaultValue = "") String texto,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "10") int tamano) {
    return PaginaDto.de(disponibilidadService.listar(farmaciaAutenticada.obtenerId(), texto, pagina, tamano));
  }

  @PutMapping("/inventario")
  public DisponibilidadFarmaciaDto guardar(@Valid @RequestBody GuardarDisponibilidadDto datos) {
    return disponibilidadService.guardar(farmaciaAutenticada.obtenerId(), datos);
  }

  @DeleteMapping("/inventario/{medicamentoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void marcarAgotado(@PathVariable Integer medicamentoId) {
    disponibilidadService.marcarAgotado(farmaciaAutenticada.obtenerId(), medicamentoId);
  }

  @GetMapping("/resumen")
  public ResumenInventarioDto resumen() {
    return disponibilidadService.resumen(farmaciaAutenticada.obtenerId());
  }

  @GetMapping("/datos")
  public DatosFarmaciaDto datos() {
    return farmaciaPanelService.obtener(farmaciaAutenticada.obtenerId());
  }

  @PutMapping("/datos")
  public DatosFarmaciaDto actualizarDatos(@Valid @RequestBody GuardarDatosFarmaciaDto datos) {
    return farmaciaPanelService.actualizar(farmaciaAutenticada.obtenerId(), datos);
  }
}