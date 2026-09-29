package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Petición de búsqueda de medicamentos públicos (capa de DTO, entrada).
 * <p>
 * Para qué sirve: recibe y valida la entrada de {@code GET /api/public/medicamentos/buscar?q=...}, así las reglas de
 * longitud quedan declaradas en un solo lugar y no como {@code if} dentro del servicio.
 * <p>
 * Cómo funciona: Spring asigna el parámetro {@code q} al componente del record con el mismo nombre. El constructor
 * compacto quita los espacios de los extremos antes de validar, así que el relleno en blanco nunca cuenta para el
 * mínimo. Después, {@code @Valid} en el controller aplica {@code @NotBlank} y {@code @Size}; el máximo es el del campo
 * buscable más largo del modelo de datos (principio activo). Que el componente se llame {@code q} hace que los errores
 * de validación nombren el parámetro que el cliente realmente envió.
 *
 * @param q texto a buscar en el nombre comercial o en el principio activo
 */
public record MedicamentoBusquedaFiltroDto(@NotBlank @Size(min = 2, max = 150) String q) {

	public MedicamentoBusquedaFiltroDto {
		q = q == null ? null : q.strip();
	}
}
