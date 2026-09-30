package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicamentoBusquedaDto(@NotBlank String id, @NotBlank @Size(min = 2, max = 100) String nombreComercial,
		@NotBlank @Size(min = 2, max = 150) String principioActivo, @NotBlank @Size(max = 100) String presentacion,
		@NotBlank @Size(max = 30) String formaFarmaceutica) {
}
