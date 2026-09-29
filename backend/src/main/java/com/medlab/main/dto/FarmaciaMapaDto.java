package com.medlab.main.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Public view of a pharmacy for the visitor map. Exposes only what the map needs; the approval state and owner are
 * never sent to the client. The name size follows the data model and the coordinates must be valid WGS84 degrees.
 */
public record FarmaciaMapaDto(@NotBlank String id, @NotBlank @Size(max = 150) String nombre,
		@DecimalMin("-90.0") @DecimalMax("90.0") double latitud,
		@DecimalMin("-180.0") @DecimalMax("180.0") double longitud) {
}
