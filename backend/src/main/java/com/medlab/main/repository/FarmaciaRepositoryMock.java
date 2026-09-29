package com.medlab.main.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.entity.Farmacia;

/**
 * Implementación temporal en memoria de {@link FarmaciaRepository} (capa de repositorios).
 * <p>
 * Para qué sirve: permite que la landing y sus endpoints funcionen de punta a punta antes de que exista la base de
 * datos. Los datos son ficticios e incluyen a propósito una farmacia pendiente y una suspendida, para comprobar que
 * solo las aprobadas son públicas.
 * <p>
 * Cómo funciona: filtra una lista fija con la misma semántica que tendrá la consulta real. Hay que borrarla cuando se
 * conecte MongoDB, siguiendo el TODO de esta clase.
 */
@Repository
public class FarmaciaRepositoryMock implements FarmaciaRepository {

	/** Farmacias ficticias en Medellín: tres aprobadas, una pendiente y una suspendida */
	private static final List<Farmacia> FARMACIAS = List.of(
			new Farmacia("1", "u-1", "Farmacia Demo El Poblado", EstadoAprobacion.APROBADA, 6.2086, -75.5659),
			new Farmacia("2", "u-2", "Farmacia Demo Laureles", EstadoAprobacion.APROBADA, 6.2447, -75.5930),
			new Farmacia("3", "u-3", "Farmacia Demo Centro", EstadoAprobacion.APROBADA, 6.2518, -75.5636),
			new Farmacia("4", "u-4", "Farmacia Demo Belén", EstadoAprobacion.PENDIENTE, 6.2308, -75.6040),
			new Farmacia("5", "u-5", "Farmacia Demo Robledo", EstadoAprobacion.SUSPENDIDA, 6.2780, -75.5960));

	@Override
	public List<Farmacia> findByEstadoAprobacion(EstadoAprobacion estadoAprobacion) {
		return FARMACIAS.stream().filter(farmacia -> farmacia.getEstadoAprobacion() == estadoAprobacion).toList();
	}
	//TODO: hay que conectarlo con la base de datos
	// 1. En pom.xml, añadir spring-boot-starter-data-mongodb y quitar JPA, Flyway y MariaDB.
	// 2. Anotar Farmacia con @Document("farmacias") e @Id en "id", y reemplazar latitud/longitud
	//    por un GeoJsonPoint "ubicacion" con @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE).
	//    Ese índice 2dsphere sustituye a idx_farmacias_ubicacion del borrador SQL.
	// 3. Hacer que FarmaciaRepository extienda MongoRepository<Farmacia, String>: findByEstadoAprobacion
	//    se deriva solo. El estado va embebido en el documento, así que el JOIN con estados_farmacia
	//    del borrador SQL se reduce a un filtro { estadoAprobacion: "APROBADA" }.
	// 4. Añadir un índice sobre estadoAprobacion para que el filtro no recorra toda la colección.
	// 5. Borrar esta clase; FarmaciaService no cambia.
}
