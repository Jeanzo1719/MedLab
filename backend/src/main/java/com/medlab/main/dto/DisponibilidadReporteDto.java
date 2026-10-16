package com.medlab.main.dto;

import java.time.LocalDateTime;

/**
 * Qué hace: lleva el reporte de disponibilidad que una farmacia hizo de un medicamento:
 * quién lo reportó, cuándo y qué tan confiable es.
 *
 * Cómo entenderlo: es como una ficha por farmacia en una lista: el nombre, hace cuánto
 * actualizó su información y un sello de confianza.
 *
 * Detalles técnicos: MedicamentoDetalleService lo arma a partir de una Disponibilidad, del
 * texto relativo que calcula TiempoRelativoService y de la confianza que calcula
 * ConfianzaService. Viaja al frontend como JSON dentro de MedicamentoDetalleDto.
 *
 * disponibilidadId: id del reporte.
 * farmacia: nombre de la farmacia que lo hizo.
 * fechaHoraReporte: fecha y hora del último reporte.
 * reporteRelativo: la misma fecha en texto relativo, por ejemplo "hace 3 horas".
 * confianza: nivel y puntaje de confianza. Es null cuando el reporte no tiene calificaciones.
 * mensajeConfianza: texto listo para mostrar: "Confianza alta", "Confianza media",
 * "Confianza baja" o "sin calificaciones aún".
 */
public record DisponibilidadReporteDto(Integer disponibilidadId, String farmacia,
    LocalDateTime fechaHoraReporte, String reporteRelativo, ConfianzaDto confianza,
    String mensajeConfianza) {
}