package com.medlab.main.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Lo que la farmacia envía para crear o editar un producto de su inventario. Lo comparten
 * HU-11 y HU-7. El estado es opcional: si no viene, el service lo deduce de la cantidad.
 */
public record GuardarDisponibilidadDto(
    @NotNull(message = "El medicamento es obligatorio")
    @Positive(message = "El medicamento no es válido")
    Integer medicamentoId,

    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    Integer cantidad,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
    BigDecimal precio,

    String estado) {
}