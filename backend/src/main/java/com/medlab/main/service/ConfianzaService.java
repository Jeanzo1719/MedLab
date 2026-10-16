package com.medlab.main.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.ConfianzaDto;
import com.medlab.main.dto.NivelConfianza;

/**
 * Qué hace: calcula qué tan confiable es un reporte de disponibilidad, combinando lo que
 * opinan los usuarios y qué tan reciente es el reporte.
 *
 * Cómo entenderlo: es como la reputación de un vendedor: cuenta cuántos clientes dicen que
 * sí había el producto, pero esa reputación se va apagando con el tiempo si no se renueva.
 *
 * Detalles técnicos: recibe cuántas calificaciones son favorables, cuántas hay en total, la
 * fecha del reporte y la hora actual, y aplica esta fórmula:
 * - proporción = favorables / total.
 * - vigencia = máximo entre 0 y (1 - horas desde el reporte / 72). Baja de 1 a 0 durante 72
 *   horas.
 * - puntaje = proporción x vigencia, redondeado a dos decimales.
 * El nivel es ALTA si el puntaje es 0.7 o más, MEDIA si es 0.4 o más y BAJA si es menor. Si
 * no hay calificaciones, no hay confianza que calcular y devuelve vacío. Este es el
 * indicador que HU-4 y HU-8 comparten. MedicamentoDetalleService lo usa una vez por reporte.
 * No consulta la base de datos.
 */
@Service
public class ConfianzaService {

  /** Nombre del tipo de calificación que cuenta a favor, escrito igual que en la base de datos. */
  public static final String TIPO_FAVORABLE = "Si hay";

  private static final double HORAS_VIGENCIA = 72.0;
  private static final double UMBRAL_ALTA = 0.7;
  private static final double UMBRAL_MEDIA = 0.4;

  /**
   * Calcula la confianza de un reporte.
   *
   * favorables: cuántas calificaciones son del tipo favorable.
   * total: cuántas calificaciones tiene el reporte.
   * fechaReporte: fecha y hora del reporte.
   * ahora: fecha y hora con la que se mide la antigüedad.
   *
   * Devuelve vacío cuando total es 0.
   */
  public Optional<ConfianzaDto> calcular(long favorables, long total, LocalDateTime fechaReporte,
      LocalDateTime ahora) {
    if (total == 0) {
      return Optional.empty();
    }
    double proporcion = (double) favorables / total;
    double horas = Math.max(0, Duration.between(fechaReporte, ahora).toSeconds()) / 3600.0;
    double vigencia = Math.max(0.0, 1.0 - horas / HORAS_VIGENCIA);
    double puntaje = Math.round(proporcion * vigencia * 100.0) / 100.0;

    return Optional.of(new ConfianzaDto(nivelPara(puntaje), puntaje));
  }

  private NivelConfianza nivelPara(double puntaje) {
    if (puntaje >= UMBRAL_ALTA) {
      return NivelConfianza.ALTA;
    }
    if (puntaje >= UMBRAL_MEDIA) {
      return NivelConfianza.MEDIA;
    }
    return NivelConfianza.BAJA;
  }
}