package com.medlab.main.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * 
 * los datos de una farmacia cercana que tiene el medicamento disponible, para
 * mostrarla en el mapa y en la lista de resultados
 * 
 * id: el número que identifica a la farmacia. Es obligatorio
 * nombre: el nombre de la farmacia. No puede estar vacío y tiene un máximo de
 * 150 caracteres
 * latitud: la posición norte-sur. Va de -90 a 90
 * longitud: la posición este-oeste. Va de -180 a 180
 * distanciaKm: qué tan lejos está del usuario, en kilómetros. Nunca es negativa
 * 
 */
public record FarmaciaCercanaDto(@NotNull Integer id, @NotBlank @Size(max = 150) String nombre,
		@DecimalMin("-90.0") @DecimalMax("90.0") double latitud,
		@DecimalMin("-180.0") @DecimalMax("180.0") double longitud, @PositiveOrZero double distanciaKm) {
}
