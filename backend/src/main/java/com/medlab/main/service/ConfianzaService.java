package com.medlab.main.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.ConfianzaDto;
import com.medlab.main.dto.NivelConfianza;

/**
 * Calcula el nivel de confianza de un reporte de disponibilidad.
 *
 * puntaje = proporción de calificaciones "Si hay" x vigencia del reporte.
 * La vigencia baja de 1 a 0 a lo largo de HORAS_VIGENCIA desde la fecha del reporte.
 */
@Service
public class ConfianzaService {

  /** Nombre del tipo de calificación que cuenta a favor (tal como está en la BD). */
  public static final String TIPO_FAVORABLE = "Si hay";

  private static final double HORAS_VIGENCIA = 72.0;
  private static final double UMBRAL_ALTA = 0.7;
  private static final double UMBRAL_MEDIA = 0.4;

  /** Devuelve vacío cuando no hay calificaciones. */
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