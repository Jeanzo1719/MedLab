package com.medlab.main.service.impl;

import com.medlab.main.service.TiempoRelativoService;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Qué hace: convierte una fecha en un texto relativo, por ejemplo "hace 3 horas".
 *
 * Cómo entenderlo: es el "hace 5 minutos" que aparece debajo de un comentario en una red
 * social: no muestra la fecha exacta, sino cuánto tiempo pasó.
 *
 * Detalles técnicos: recibe la fecha del reporte y la hora actual, calcula la diferencia y
 * elige la unidad: menos de 1 minuto, minutos (menos de 1 hora), horas (menos de 1 día),
 * días (menos de 30 días), meses (menos de 365 días) y años. Si la fecha es posterior a la
 * hora actual, la trata como si fuera ahora. No consulta la base de datos ni guarda el
 * resultado. MedicamentoDetalleServiceImpl lo usa para llenar reporteRelativo en cada
 * DisponibilidadReporteDto.
 */
@Service
public class TiempoRelativoServiceImpl implements TiempoRelativoService {

  @Override
  public String getTiempoRelativo(LocalDateTime fecha, LocalDateTime ahora) {
    long segundos = Math.max(0, Duration.between(fecha, ahora).toSeconds());
    long minutos = segundos / 60;
    long horas = minutos / 60;
    long dias = horas / 24;

    if (minutos < 1) {
      return "hace unos segundos";
    }
    if (minutos < 60) {
      return "hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
    }
    if (horas < 24) {
      return "hace " + horas + (horas == 1 ? " hora" : " horas");
    }
    if (dias < 30) {
      return "hace " + dias + (dias == 1 ? " día" : " días");
    }
    if (dias < 365) {
      long meses = dias / 30;
      return "hace " + meses + (meses == 1 ? " mes" : " meses");
    }
    long anios = dias / 365;
    return "hace " + anios + (anios == 1 ? " año" : " años");
  }
}
