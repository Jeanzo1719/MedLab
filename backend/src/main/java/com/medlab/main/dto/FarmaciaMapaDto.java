package com.medlab.main.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 
 * los datos de una farmacia que se envían para dibujar su marcador en el mapa
 * 
 * id: el número que identifica a la farmacia. Es obligatorio
 * nombre: el nombre de la farmacia, el que se ve al tocar el marcador. No puede 
 * estar vacío y tiene un máximo de 150 caracteres
 * latitud: la posición norte-sur. Va de -90 a 90, que son los límites reales de 
 * la Tierra (los polos)
 * longitud: la posición este-oeste. Va de -180 a 180, que es la vuelta completa
 * al planeta
 */
public record FarmaciaMapaDto(@NotNull Integer id, @NotBlank @Size(max = 150) String nombre,
		@DecimalMin("-90.0") @DecimalMax("90.0") double latitud,
		@DecimalMin("-180.0") @DecimalMax("180.0") double longitud) {
}
