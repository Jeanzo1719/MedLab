package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Search request for public medicines, bound from the {@code q} query parameter. The text is stripped before being
 * validated, so blank padding never counts towards the minimum; the maximum is the longest searchable field (active
 * ingredient). The component is named {@code q}, like the query parameter, so validation errors name the parameter the
 * client actually sent.
 */
public record MedicamentoBusquedaFiltroDto(@NotBlank @Size(min = 2, max = 150) String q) {

	public MedicamentoBusquedaFiltroDto {
		q = q == null ? null : q.strip();
	}
}
