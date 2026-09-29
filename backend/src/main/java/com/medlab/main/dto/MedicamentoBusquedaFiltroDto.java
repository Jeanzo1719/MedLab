package com.medlab.main.dto;

import org.springframework.web.bind.annotation.BindParam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Search request for public medicines, bound from the {@code q} query parameter. The text is stripped before being
 * validated, so blank padding never counts towards the minimum; the maximum is the longest searchable field (active
 * ingredient).
 */
public record MedicamentoBusquedaFiltroDto(@BindParam("q") @NotBlank @Size(min = 2, max = 150) String texto) {

	public MedicamentoBusquedaFiltroDto {
		texto = texto == null ? null : texto.strip();
	}
}
