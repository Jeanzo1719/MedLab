package com.medlab.main.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Lo que la farmacia envía al reportar a mano un medicamento (HU-7): los datos del medicamento
 * más cantidad, precio y estado. El estado es opcional, igual que en HU-11.
 */
public record GuardarDisponibilidadManualDto(
    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre comercial debe tener entre 2 y 100 caracteres")
    String nombreComercial,

    @NotBlank(message = "El principio activo es obligatorio")
    @Size(min = 2, max = 150, message = "El principio activo debe tener entre 2 y 150 caracteres")
    String principioActivo,

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "La categoría no es válida")
    Integer categoriaId,

    @NotBlank(message = "La presentación es obligatoria")
    @Size(max = 100, message = "La presentación admite hasta 100 caracteres")
    String presentacion,

    @NotNull(message = "La forma farmacéutica es obligatoria")
    @Positive(message = "La forma farmacéutica no es válida")
    Integer formaFarmaceuticaId,

    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    Integer cantidad,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
    BigDecimal precio,

    String estado) {
}