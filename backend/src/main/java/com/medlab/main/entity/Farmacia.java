package com.medlab.main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Farmacia del modelo de dominio (capa de entidades).
 * <p>
 * Para qué sirve: es la representación interna de una farmacia, tal como se guardará en la colección "farmacias" de
 * MongoDB. La API nunca la devuelve directamente: {@link com.medlab.main.mapper.FarmaciaMapper} la convierte en el DTO
 * público.
 * <p>
 * Cómo funciona: es una clase simple con getters, setters y constructores generados por Lombok. Cuando se conecte
 * MongoDB se anotará con {@code @Document}; los pasos están en el TODO de
 * {@link com.medlab.main.repository.FarmaciaRepositoryMock}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Farmacia {

	private String id;
	/** Cuenta de usuario dueña de la farmacia; nunca se muestra a los visitantes */
	private String usuarioId;
	private String nombreFarmacia;
	/** Solo las farmacias {@link EstadoAprobacion#APROBADA} son públicas */
	private EstadoAprobacion estadoAprobacion;
	/** Coordenadas WGS84 en grados; con MongoDB pasarán a ser un punto GeoJSON */
	private double latitud;
	private double longitud;
}
