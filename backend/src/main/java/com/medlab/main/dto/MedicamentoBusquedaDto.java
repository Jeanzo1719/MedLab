package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Vista pública de un medicamento en los resultados de búsqueda (capa de DTO, respuesta).
 * <p>
 * Para qué sirve: es la forma exacta del JSON que devuelve {@code GET /api/public/medicamentos/buscar}, y el frontend
 * la replica en {@code medicamento-busqueda.model.ts}. Deja fuera campos internos de la entidad, como la categoría.
 * <p>
 * Cómo funciona: es un record inmutable que llena {@link com.medlab.main.mapper.MedicamentoMapper}. Sus tamaños siguen
 * los límites del medicamento en el modelo de datos.
 *
 * @param id                identificador del medicamento
 * @param nombreComercial   nombre comercial, de 2 a 100 caracteres
 * @param principioActivo   principio activo, de 2 a 150 caracteres
 * @param presentacion      presentación del empaque, por ejemplo "500 mg x 20"
 * @param formaFarmaceutica forma farmacéutica, por ejemplo "Tableta"
 */
public record MedicamentoBusquedaDto(@NotBlank String id, @NotBlank @Size(min = 2, max = 100) String nombreComercial,
		@NotBlank @Size(min = 2, max = 150) String principioActivo, @NotBlank @Size(max = 100) String presentacion,
		@NotBlank @Size(max = 30) String formaFarmaceutica) {
}
