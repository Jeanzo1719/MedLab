package com.medlab.main.dto;

import java.util.List;

/** Información básica de un medicamento y sus reportes de disponibilidad, uno por farmacia. */
public record MedicamentoDetalleDto(Integer id, String nombreComercial, String principioActivo,
    String categoria, String presentacion, String formaFarmaceutica,
    List<DisponibilidadReporteDto> reportes) {
}