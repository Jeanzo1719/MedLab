package com.medlab.main.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Vista pública de una farmacia para el mapa de visitantes (capa de DTO, respuesta).
 * <p>
 * Para qué sirve: es la forma exacta del JSON que devuelve {@code GET /api/public/farmacias/aprobadas}, y el frontend
 * la replica en {@code farmacia-mapa.model.ts}. Exponer un DTO en lugar de la entidad
 * {@link com.medlab.main.entity.Farmacia} garantiza que el estado de aprobación y el dueño nunca se envían al cliente.
 * <p>
 * Cómo funciona: es un record inmutable que llena {@link com.medlab.main.mapper.FarmaciaMapper}. Sus restricciones
 * documentan el contrato: el tamaño del nombre sigue el modelo de datos y las coordenadas deben ser grados WGS84
 * válidos.
 *
 * @param id       identificador de la farmacia
 * @param nombre   nombre de la farmacia, que se muestra en el tooltip del marcador
 * @param latitud  latitud en grados, entre -90 y 90
 * @param longitud longitud en grados, entre -180 y 180
 */
public record FarmaciaMapaDto(@NotBlank String id, @NotBlank @Size(max = 150) String nombre,
		@DecimalMin("-90.0") @DecimalMax("90.0") double latitud,
		@DecimalMin("-180.0") @DecimalMax("180.0") double longitud) {
}
