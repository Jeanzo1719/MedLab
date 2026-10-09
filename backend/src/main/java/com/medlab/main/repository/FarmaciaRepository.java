package com.medlab.main.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medlab.main.entity.Farmacia;

/**
 * 
 * este es el repositorio que da acceso a la tabla farmacias
 * 
 * devuelve la lista de farmacias que están en el estado que se le solicita
 * 
 */
public interface FarmaciaRepository extends JpaRepository<Farmacia, Integer> {

	List<Farmacia> findByEstadoAprobacionNombre(String nombre);

	@Query(value = """
			SELECT f.usuario_id AS id, f.nombre_farmacia AS nombre, f.latitud AS latitud, f.longitud AS longitud,
			       6371 * 2 * ASIN(SQRT(
			           POWER(SIN(RADIANS(f.latitud - :latitud) / 2), 2)
			           + COS(RADIANS(:latitud)) * COS(RADIANS(f.latitud))
			           * POWER(SIN(RADIANS(f.longitud - :longitud) / 2), 2))) AS distanciaKm
			FROM farmacias f
			JOIN estados_farmacia ef ON ef.id = f.estado_aprobacion_id
			JOIN disponibilidad d ON d.farmacia_id = f.usuario_id
			JOIN estados_disponibilidad ed ON ed.id = d.estado_disponibilidad_id
			WHERE ef.nombre = :estadoFarmacia
			  AND d.medicamento_id = :medicamentoId
			  AND ed.nombre <> :estadoExcluido
			  AND f.latitud BETWEEN :latitud - :radioKm / 111.045 AND :latitud + :radioKm / 111.045
			  AND f.longitud BETWEEN :longitud - :radioKm / (111.045 * COS(RADIANS(:latitud)))
			                     AND :longitud + :radioKm / (111.045 * COS(RADIANS(:latitud)))
			HAVING distanciaKm <= :radioKm
			ORDER BY distanciaKm
			LIMIT :limite
			""", nativeQuery = true)
	List<FarmaciaCercana> buscarCercanasConDisponibilidad(@Param("estadoFarmacia") String estadoFarmacia,
			@Param("estadoExcluido") String estadoExcluido, @Param("medicamentoId") Integer medicamentoId,
			@Param("latitud") double latitud, @Param("longitud") double longitud, @Param("radioKm") double radioKm,
			@Param("limite") int limite);

	interface FarmaciaCercana {

		Integer getId();

		String getNombre();

		BigDecimal getLatitud();

		BigDecimal getLongitud();

		Double getDistanciaKm();
	}
}
