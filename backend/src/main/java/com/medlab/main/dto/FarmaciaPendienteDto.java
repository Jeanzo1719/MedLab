package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FarmaciaPendienteDto(@NotNull Integer id, @NotBlank @Size(max = 150) String nombre,
		@NotBlank @Size(max = 20) String nit, @NotBlank @Size(max = 20) String rut,
		@NotBlank @Size(max = 20) String matriculaMercantil,
		@NotBlank @Size(max = 10) String identificacionDirectorTecnico,
		@NotBlank @Size(max = 50) String licenciaSanitaria) {
}
