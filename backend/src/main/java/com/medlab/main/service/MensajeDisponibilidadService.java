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
 * se ponen de acuerdo. Si el reporte de la farmacia es viejo, el letrero lleva además una nota
 * que avisa que puede estar desactualizado.
 */
public interface MensajeDisponibilidadService {

  /** Cuántas calificaciones, las más recientes de cada reporte, se tienen en cuenta. */
  int CALIFICACIONES_RECIENTES = 20;

  /** Nombres de los tipos de calificación, escritos igual que en la base de datos. */
  String TIPO_SI_HAY = "Si hay";
  String TIPO_BAJO_PEDIDO = "Bajo pedido";
  String TIPO_NO_HAY = "No hay";

  /**
   * Cuántos días puede tener un reporte de disponibilidad antes de que su mensaje lleve el
   * aviso de que puede estar desactualizado.
   */
  int DIAS_REPORTE_VIGENTE = 15;

  /**
   * Calcula la tendencia de disponibilidad de un reporte.
   *
   * tiposRecientes: nombres de los tipos de las calificaciones recientes del reporte. Puede
   * estar vacía.
   */
  TendenciaDisponibilidad calculateTendenciaDisponibilidad(List<String> tiposRecientes);

  /**
   * Arma el mensaje que se muestra para un reporte.
   *
   * tendencia: lo que dicen sus calificaciones recientes.
   * fechaHoraReporte: cuándo la farmacia actualizó el reporte por última vez.
   * ahora: momento con el que se compara.
   *
   * Devuelve el mensaje de la tendencia. Si el reporte tiene más de DIAS_REPORTE_VIGENTE
   * días, le agrega "El reporte de la farmacia tiene más de 15 días y puede estar
   * desactualizado".
   */
  String buildMensajeDisponibilidad(TendenciaDisponibilidad tendencia,
      LocalDateTime fechaHoraReporte, LocalDateTime ahora);
}
