package com.medlab.main.dto;

/**
 * MedicamentoResponseDto
 */
public record MedicamentoResponseDto(
    Long id,
    String nombreComercial,
    String principioActivo,
    Long categoriaId,
    String categoriaNombre,
    String presentacion,
    Long formaFarmaceuticaId,
    String formaFarmaceuticaNombre) {
}
