package com.medlab.main.dto;

/** Public view of a medicine in the visitor search results. */
public record MedicamentoBusquedaDto(
    String id,
    String nombreComercial,
    String principioActivo,
    String presentacion,
    String formaFarmaceutica) {}
