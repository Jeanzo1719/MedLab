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
 * y las farmacias aprobadas más cercanas que tienen un medicamento disponible
 * 
 */
public interface FarmaciaRepository extends JpaRepository<Farmacia, Integer> {

	List<Farmacia> findByEstadoAprobacionNombre(String nombre);

	/**
	 * 
	 * busca las farmacias aprobadas que tienen el medicamento disponible, de la
	 * más cercana a la más lejana, sin pasar del radio ni del límite que se le pidan
	 * 
	 * une cada farmacia con su estado y con la tabla disponibilidad, y se queda
	 * solo con las que tienen el estado de farmacia pedido (aprobada), el
	 * medicamento buscado y un estado de disponibilidad distinto al excluido
	 * (agotado). Primero descarta las que quedan fuera de un cuadro alrededor del
	 * usuario (un grado de latitud mide unos 111 km); así usa el índice
	 * idx_farmacias_ubicacion y no revisa todas las farmacias. Después calcula la
	 * distancia real con la fórmula de Haversine (6371 es el radio de la Tierra en
	 * km), quita las que pasan del radio, las ordena por distancia y se queda con
	 * las primeras según el límite
	 * 
	 */
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

	/**
	 * 
	 * la forma de cada fila que devuelve la búsqueda de farmacias cercanas
	 * 
	 * no es una tabla. Spring llena cada método con la columna de la consulta que
	 * tiene el mismo nombre: id, nombre, latitud, longitud y distanciaKm
	 * 
	 */
	interface FarmaciaCercana {

		Integer getId();

		String getNombre();

		BigDecimal getLatitud();

		BigDecimal getLongitud();

		Double getDistanciaKm();
	}
}
