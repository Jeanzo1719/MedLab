package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Los datos para crear o editar un medicamento. Todos son obligatorios y sus largos son los de la
 * tabla medicamentos: si algo no cumple, se responde 400 sin llegar a la base de datos.
 *
 * nombreComercial: el nombre de marca, de 2 a 100 caracteres.
 * principioActivo: la sustancia que hace efecto, de 2 a 150 caracteres.
 * categoriaId: el id de la categoría.
 * presentacion: cómo viene empacado, hasta 100 caracteres.
 * formaFarmaceuticaId: el id de la forma farmacéutica.
 */
public record MedicamentoRequestDto(
    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre comercial debe tener entre 2 y 100 caracteres")
    String nombreComercial,

    @NotBlank(message = "El principio activo es obligatorio")
    @Size(min = 2, max = 150, message = "El principio activo debe tener entre 2 y 150 caracteres")
    String principioActivo,

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "La categoría no es válida")
    Long categoriaId,

    @NotBlank(message = "La presentación es obligatoria")
    @Size(max = 100, message = "La presentación no puede pasar de 100 caracteres")
    String presentacion,

    @NotNull(message = "La forma farmacéutica es obligatoria")
    @Positive(message = "La forma farmacéutica no es válida")
    Long formaFarmaceuticaId) {
}
