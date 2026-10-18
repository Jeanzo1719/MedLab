package com.medlab.main.dto;

/**
 * MedicamentRequestDto
 */
public record MedicamentoRequestDto(
    String nombreComercial,
    String principioActivo,
    Long categoriaId,
    String presentacion,
    Long formaFarmaceuticaId) {
}
