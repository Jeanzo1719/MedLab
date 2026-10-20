package com.medlab.main.dto;

import java.time.LocalDateTime;

/**
 * Qué hace: lleva el reporte de disponibilidad que una farmacia hizo de un medicamento:
 * quién lo reportó, cuándo y qué dicen las calificaciones recientes.
 *
 * Cómo entenderlo: es como una ficha por farmacia en una lista: el nombre, hace cuánto
 * actualizó su información y un resumen de lo que opinan los últimos usuarios.
 *
 * Detalles técnicos: MedicamentoDetalleServiceImpl lo arma a partir de una
 * DisponibilidadEntity, del texto relativo que calcula TiempoRelativoService y de la
 * tendencia que calcula MensajeDisponibilidadService. Viaja al frontend como JSON dentro de
 * MedicamentoDetalleDto.
 *
 * disponibilidadId: id del reporte.
 * farmacia: nombre de la farmacia que lo hizo.
 * fechaHoraReporte: fecha y hora del último reporte.
 * reporteRelativo: la misma fecha en texto relativo, por ejemplo "hace 3 horas".
 * tendencia: lo que dicen las calificaciones recientes (DISPONIBLE, BAJO_PEDIDO,
 * NO_DISPONIBLE, SIN_CONSENSO o SIN_CALIFICACIONES).
 * mensajeDisponibilidad: texto de la tendencia listo para mostrar, por ejemplo "Según los
 * últimos reportes, el medicamento está disponible" o "sin calificaciones aún".
 */
public record DisponibilidadReporteDto(Integer disponibilidadId, String farmacia,
    LocalDateTime fechaHoraReporte, String reporteRelativo, TendenciaDisponibilidad tendencia,
    String mensajeDisponibilidad) {
}