package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de TiempoRelativoService: comprueban cada unidad de tiempo (segundos,
 * minutos, horas, días, meses y años), el singular y el plural, los límites entre unidades y
 * el caso de una fecha futura. No usan base de datos.
 */
class TiempoRelativoServiceTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 16, 12, 0, 0);

  private final TiempoRelativoService servicio = new TiempoRelativoService();

  @Test
  void treintaSegundosDiceHaceUnosSegundos() {
    assertEquals("hace unos segundos", servicio.desde(AHORA.minusSeconds(30), AHORA));
  }

  @Test
  void ahoraMismoDiceHaceUnosSegundos() {
    assertEquals("hace unos segundos", servicio.desde(AHORA, AHORA));
  }

  @Test
  void unMinutoUsaSingular() {
    assertEquals("hace 1 minuto", servicio.desde(AHORA.minusSeconds(60), AHORA));
  }

  @Test
  void cincuentaYNueveMinutosSigueSiendoMinutos() {
    assertEquals("hace 59 minutos", servicio.desde(AHORA.minusMinutes(59), AHORA));
  }

  @Test
  void sesentaMinutosEsUnaHora() {
    assertEquals("hace 1 hora", servicio.desde(AHORA.minusMinutes(60), AHORA));
  }

  @Test
  void dosHorasUsaPlural() {
    assertEquals("hace 2 horas", servicio.desde(AHORA.minusHours(2), AHORA));
  }

  @Test
  void veintitresHorasYMediaSigueSiendoHoras() {
    assertEquals("hace 23 horas",
        servicio.desde(AHORA.minusHours(23).minusMinutes(59), AHORA));
  }

  @Test
  void veinticuatroHorasEsUnDia() {
    assertEquals("hace 1 día", servicio.desde(AHORA.minusHours(24), AHORA));
  }

  @Test
  void veintinueveDiasSigueSiendoDias() {
    assertEquals("hace 29 días", servicio.desde(AHORA.minusDays(29), AHORA));
  }

  @Test
  void treintaDiasEsUnMes() {
    assertEquals("hace 1 mes", servicio.desde(AHORA.minusDays(30), AHORA));
  }

  @Test
  void sesentaDiasSonDosMeses() {
    assertEquals("hace 2 meses", servicio.desde(AHORA.minusDays(60), AHORA));
  }

  @Test
  void trescientosSesentaYCuatroDiasSigueSiendoMeses() {
    assertEquals("hace 12 meses", servicio.desde(AHORA.minusDays(364), AHORA));
  }

  @Test
  void trescientosSesentaYCincoDiasEsUnAnio() {
    assertEquals("hace 1 año", servicio.desde(AHORA.minusDays(365), AHORA));
  }

  @Test
  void setecientosTreintaDiasSonDosAnios() {
    assertEquals("hace 2 años", servicio.desde(AHORA.minusDays(730), AHORA));
  }

  @Test
  void fechaFuturaSeTrataComoAhora() {
    assertEquals("hace unos segundos", servicio.desde(AHORA.plusHours(5), AHORA));
  }
}