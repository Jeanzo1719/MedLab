package com.medlab.main.dto;

import java.time.LocalDateTime;

/**
 * Un reporte de disponibilidad de una farmacia para el medicamento.
 *
 * confianza: es null cuando el reporte no tiene calificaciones.
 * mensajeConfianza: texto listo para mostrar ("Confianza alta" o "sin calificaciones aún").
 */
public record DisponibilidadReporteDto(Integer disponibilidadId, String farmacia,
    LocalDateTime fechaHoraReporte, String reporteRelativo, ConfianzaDto confianza,
    String mensajeConfianza) {
}