package com.medlab.main.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record FarmaciaCercanaDto(@NotNull Integer id, @NotBlank @Size(max = 150) String nombre,
		@DecimalMin("-90.0") @DecimalMax("90.0") double latitud,
		@DecimalMin("-180.0") @DecimalMax("180.0") double longitud, @PositiveOrZero double distanciaKm) {
}
