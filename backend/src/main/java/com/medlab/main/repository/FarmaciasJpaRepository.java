package com.medlab.main.repository;

import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.repository.FarmaciasRepository.FarmaciaCercana;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmaciasJpaRepository extends JpaRepository<FarmaciasEntity, Integer> {

    List<FarmaciasEntity> findByEstadoAprobacionNombre(String nombreEstado);

    /**
     * Busca las farmacias aprobadas que tienen el medicamento disponible, de la mas cercana
     * a la mas lejana, sin pasar del radio ni del limite pedido.
     *
     * Es una consulta nativa porque calcula la distancia real con la formula de Haversine
     * (6371 es el radio de la Tierra en km), lo que no se puede expresar como consulta
     * derivada. Primero descarta las farmacias fuera de un cuadro alrededor del usuario (un
     * grado de latitud mide unos 111 km) para usar el indice idx_farmacias_ubicacion; luego
     * calcula la distancia, quita las que pasan del radio, las ordena y se queda con las
     * primeras segun el limite.
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
    List<FarmaciaCercana> findFarmaciasCercanasConDisponibilidad(@Param("estadoFarmacia") String estadoFarmacia,
            @Param("estadoExcluido") String estadoExcluido, @Param("medicamentoId") Long medicamentoId,
            @Param("latitud") double latitud, @Param("longitud") double longitud, @Param("radioKm") double radioKm,
            @Param("limite") int limite);
}
