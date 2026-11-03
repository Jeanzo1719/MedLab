package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

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
 * categoria: el nombre de la categoría o uso general, por ejemplo "Analgésico"
 * precioMinimo: el precio más bajo entre las farmacias aprobadas que lo reportan.
 * Es null si ninguna farmacia aprobada lo ha reportado.
 * farmaciasDisponibles: cuántas farmacias aprobadas lo tienen en estado disponible
 * farmaciasReportaron: cuántas farmacias aprobadas lo han reportado
 * 
 */
public record MedicamentoBusquedaDto(@NotNull Long id, @NotBlank @Size(min = 2, max = 100) String nombreComercial,
		@NotBlank @Size(min = 2, max = 150) String principioActivo, @NotBlank @Size(max = 100) String presentacion,
		@NotBlank @Size(max = 30) String formaFarmaceutica, @NotBlank @Size(max = 100) String categoria,
		BigDecimal precioMinimo, @NotNull Long farmaciasDisponibles, @NotNull Long farmaciasReportaron) {
}
