package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import com.medlab.main.service.impl.TiempoRelativoServiceImpl;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de TiempoRelativoService: comprueban cada unidad de tiempo (segundos,
 * minutos, horas, días, meses y años), el singular y el plural, los límites entre unidades y
 * el caso de una fecha futura. No usan base de datos.
 */
class TiempoRelativoServiceTest {

  private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 16, 12, 0, 0);

  private final TiempoRelativoService servicio = new TiempoRelativoServiceImpl();

  @Test
  void treintaSegundosDiceHaceUnosSegundos() {
    assertEquals("hace unos segundos", servicio.getTiempoRelativo(AHORA.minusSeconds(30), AHORA));
  }

  @Test
  void ahoraMismoDiceHaceUnosSegundos() {
    assertEquals("hace unos segundos", servicio.getTiempoRelativo(AHORA, AHORA));
  }

  @Test
  void unMinutoUsaSingular() {
    assertEquals("hace 1 minuto", servicio.getTiempoRelativo(AHORA.minusSeconds(60), AHORA));
  }

  @Test
  void cincuentaYNueveMinutosSigueSiendoMinutos() {
    assertEquals("hace 59 minutos", servicio.getTiempoRelativo(AHORA.minusMinutes(59), AHORA));
  }

  @Test
  void sesentaMinutosEsUnaHora() {
    assertEquals("hace 1 hora", servicio.getTiempoRelativo(AHORA.minusMinutes(60), AHORA));
  }

  @Test
  void dosHorasUsaPlural() {
    assertEquals("hace 2 horas", servicio.getTiempoRelativo(AHORA.minusHours(2), AHORA));
  }

  @Test
  void veintitresHorasYMediaSigueSiendoHoras() {
    assertEquals("hace 23 horas",
        servicio.getTiempoRelativo(AHORA.minusHours(23).minusMinutes(59), AHORA));
  }

  @Test
  void veinticuatroHorasEsUnDia() {
    assertEquals("hace 1 día", servicio.getTiempoRelativo(AHORA.minusHours(24), AHORA));
  }

  @Test
  void veintinueveDiasSigueSiendoDias() {
    assertEquals("hace 29 días", servicio.getTiempoRelativo(AHORA.minusDays(29), AHORA));
  }

  @Test
  void treintaDiasEsUnMes() {
    assertEquals("hace 1 mes", servicio.getTiempoRelativo(AHORA.minusDays(30), AHORA));
  }

  @Test
  void sesentaDiasSonDosMeses() {
    assertEquals("hace 2 meses", servicio.getTiempoRelativo(AHORA.minusDays(60), AHORA));
  }

  @Test
  void trescientosSesentaYCuatroDiasSigueSiendoMeses() {
    assertEquals("hace 12 meses", servicio.getTiempoRelativo(AHORA.minusDays(364), AHORA));
  }

  @Test
  void trescientosSesentaYCincoDiasEsUnAnio() {
    assertEquals("hace 1 año", servicio.getTiempoRelativo(AHORA.minusDays(365), AHORA));
  }

  @Test
  void setecientosTreintaDiasSonDosAnios() {
    assertEquals("hace 2 años", servicio.getTiempoRelativo(AHORA.minusDays(730), AHORA));
  }

  @Test
  void fechaFuturaSeTrataComoAhora() {
    assertEquals("hace unos segundos", servicio.getTiempoRelativo(AHORA.plusHours(5), AHORA));
  }
}