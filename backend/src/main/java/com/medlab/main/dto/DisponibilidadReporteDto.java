package com.medlab.main.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Qué hace: lleva el reporte de disponibilidad que una farmacia hizo de un medicamento:
 * quién lo reportó, cuándo, a qué precio, en qué estado y qué dicen las calificaciones
 * recientes.
 *
 * Cómo entenderlo: es como una ficha por farmacia en una lista: el nombre, hace cuánto
 * actualizó su información, cuánto cuesta, si hay o no y un resumen de lo que opinan los
 * últimos usuarios.
 *
 * Detalles técnicos: MedicamentoDetalleServiceImpl lo arma a partir de una
 * DisponibilidadEntity, del texto relativo que calcula TiempoRelativoService y de la
 * tendencia que calcula MensajeDisponibilidadService. La confianza es el porcentaje de
 * calificaciones recientes que apoyan la tendencia ganadora. Viaja al frontend como JSON
 * dentro de MedicamentoDetalleDto.
 *
 * disponibilidadId: id del reporte.
 * farmacia: nombre de la farmacia que lo hizo.
 * fechaHoraReporte: fecha y hora del último reporte.
 * reporteRelativo: la misma fecha en texto relativo, por ejemplo "hace 3 horas".
 * tendencia: lo que dicen las calificaciones recientes (DISPONIBLE, BAJO_PEDIDO,
 * NO_DISPONIBLE, SIN_CONSENSO o SIN_CALIFICACIONES).
 * mensajeDisponibilidad: texto de la tendencia listo para mostrar, por ejemplo "Según los
 * últimos reportes, el medicamento está disponible" o "sin calificaciones aún".
 * precio: precio reportado por la farmacia.
 * cantidadDisponible: unidades que la farmacia dice tener.
 * estado: estado que reportó la farmacia: disponible, agotado o bajo_pedido.
 * confianza: porcentaje (0 a 100) de calificaciones recientes que apoyan la tendencia. Es 0
 * si el reporte no tiene calificaciones.
 */
public record DisponibilidadReporteDto(Integer disponibilidadId, String farmacia,
    LocalDateTime fechaHoraReporte, String reporteRelativo, TendenciaDisponibilidad tendencia,
    String mensajeDisponibilidad, BigDecimal precio, Integer cantidadDisponible, String estado,
    int confianza) {
}
