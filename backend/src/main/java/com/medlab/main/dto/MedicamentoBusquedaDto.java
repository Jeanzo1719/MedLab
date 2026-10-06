package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 
 * los datos de un medicamento que se envían para mostrar cada resultado del buscador
 * 
 * id: el número que identifica al medicamento. Es obligatorio
 * nombreComercial: el nombre de marca. Tiene entre 2 y 100 caracteres
 * principioActivo: la sustancia que hace efecto. Tiene entre 2 y 150 caracteres
 * presentacion: cómo viene empacado. Tiene un máximo de 100 caracteres
 * formaFarmaceutica: el nombre de la forma, por ejemplo "Tableta". Tiene un máximo 
 * de 30 caracteres. Aquí es solo un texto, no un objeto, porque el service ya sacó
 * el nombre.
 * 
 */
public record MedicamentoBusquedaDto(@NotNull Integer id, @NotBlank @Size(min = 2, max = 100) String nombreComercial,
		@NotBlank @Size(min = 2, max = 150) String principioActivo, @NotBlank @Size(max = 100) String presentacion,
		@NotBlank @Size(max = 30) String formaFarmaceutica) {
}
