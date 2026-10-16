package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.medlab.main.dto.TendenciaDisponibilidad;

/**
 * Pruebas unitarias de MensajeDisponibilidadService: comprueban que el tipo con más votos
 * decide la tendencia, que los empates dan SIN_CONSENSO y que sin votos válidos no hay
 * calificaciones. No usan base de datos.
 */
class MensajeDisponibilidadServiceTest {

  private final MensajeDisponibilidadService servicio = new MensajeDisponibilidadService();

  @Test
  void sinCalificacionesDevuelveSinCalificaciones() {
    assertEquals(TendenciaDisponibilidad.SIN_CALIFICACIONES, servicio.calcular(List.of()));
  }

  @Test
  void mayoriaSiHayDevuelveDisponible() {
    assertEquals(TendenciaDisponibilidad.DISPONIBLE,
        servicio.calcular(List.of("Si hay", "Si hay", "No hay")));
  }

  @Test
  void mayoriaBajoPedidoDevuelveBajoPedido() {
    assertEquals(TendenciaDisponibilidad.BAJO_PEDIDO,
        servicio.calcular(List.of("Bajo pedido", "Si hay", "Bajo pedido")));
  }

  @Test
  void mayoriaNoHayDevuelveNoDisponible() {
    assertEquals(TendenciaDisponibilidad.NO_DISPONIBLE,
        servicio.calcular(List.of("No hay", "No hay", "Si hay", "Bajo pedido")));
  }

  @Test
  void todasSiHayDevuelveDisponible() {
    assertEquals(TendenciaDisponibilidad.DISPONIBLE,
        servicio.calcular(List.of("Si hay", "Si hay", "Si hay", "Si hay", "Si hay")));
  }

  @Test
  void empateEntreDosTiposDevuelveSinConsenso() {
    assertEquals(TendenciaDisponibilidad.SIN_CONSENSO,
        servicio.calcular(List.of("Si hay", "No hay")));
  }

  @Test
  void empateEntreLosTresTiposDevuelveSinConsenso() {
    assertEquals(TendenciaDisponibilidad.SIN_CONSENSO,
        servicio.calcular(List.of("Si hay", "Bajo pedido", "No hay")));
  }

  @Test
  void tiposDesconocidosSeIgnoran() {
    assertEquals(TendenciaDisponibilidad.DISPONIBLE,
        servicio.calcular(List.of("Otro", "Otro", "Si hay")));
  }

  @Test
  void soloTiposDesconocidosDevuelveSinCalificaciones() {
    assertEquals(TendenciaDisponibilidad.SIN_CALIFICACIONES,
        servicio.calcular(List.of("Otro")));
  }
}