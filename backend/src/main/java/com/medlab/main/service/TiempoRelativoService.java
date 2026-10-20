package com.medlab.main.service;

import java.time.LocalDateTime;

/**
 * Qué hace: convierte una fecha en un texto relativo, por ejemplo "hace 3 horas".
 *
 * Cómo entenderlo: es el "hace 5 minutos" que aparece debajo de un comentario en una red
 * social: no muestra la fecha exacta, sino cuánto tiempo pasó.
 */
public interface TiempoRelativoService {

  /**
   * Devuelve cuánto tiempo pasó entre la fecha y la hora actual, por ejemplo "hace 2 horas".
   *
   * fecha: momento del reporte.
   * ahora: momento con el que se compara.
   */
  String getTiempoRelativo(LocalDateTime fecha, LocalDateTime ahora);
}
