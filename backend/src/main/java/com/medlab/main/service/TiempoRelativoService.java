package com.medlab.main.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

/** Convierte una fecha en texto relativo, por ejemplo "hace 3 horas". */
@Service
public class TiempoRelativoService {

  public String desde(LocalDateTime fecha, LocalDateTime ahora) {
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