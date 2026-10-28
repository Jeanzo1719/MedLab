package com.medlab.main.service;

import com.medlab.main.dto.TendenciaDisponibilidad;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Qué hace: decide qué dicen las calificaciones recientes de un reporte sobre la
 * disponibilidad del medicamento.
 *
 * Cómo entenderlo: es como contar los votos de los últimos clientes: si la mayoría dice "sí
 * hay", el letrero dice que está disponible; si dos opciones empatan, el letrero dice que no
 * se ponen de acuerdo.
 */
public interface MensajeDisponibilidadService {

  /** Cuántas calificaciones, las más recientes de cada reporte, se tienen en cuenta. */
  int CALIFICACIONES_RECIENTES = 20;

  /** Nombres de los tipos de calificación, escritos igual que en la base de datos. */
  String TIPO_SI_HAY = "Si hay";
  String TIPO_BAJO_PEDIDO = "Bajo pedido";
  String TIPO_NO_HAY = "No hay";

  int DIAS_REPORTE_VIGENTE = 15;

  /**
   * Calcula la tendencia de disponibilidad de un reporte.
   *
   * tiposRecientes: nombres de los tipos de las calificaciones recientes del reporte. Puede
   * estar vacía.
   */
  TendenciaDisponibilidad calculateTendenciaDisponibilidad(List<String> tiposRecientes);

  String buildMensajeDisponibilidad(TendenciaDisponibilidad tendencia,
      LocalDateTime fechaHoraReporte, LocalDateTime ahora);
}
