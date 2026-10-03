package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicamentoBusquedaFiltroDto(@NotBlank @Size(min = 2, max = 150) String q) {

	public MedicamentoBusquedaFiltroDto {
		q = q == null ? null : q.strip();
	}
}
