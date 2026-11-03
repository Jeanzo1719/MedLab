package com.medlab.main.repository;

import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.repository.MedicamentosRepository.MedicamentoBusquedaProyeccion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentosJpaRepository extends JpaRepository<MedicamentosEntity, Long> {

    Optional<MedicamentosEntity> findByNombreComercial(String nombreComercial);

    Optional<MedicamentosEntity> findByPrincipioActivo(String principioActivo);

    List<MedicamentosEntity> findByPrincipioActivoAndIdNot(String principioActivo, Long id);

    /** Trae el medicamento con su categoria y su formaFarmaceutica en una sola consulta. */
    @Override
    @EntityGraph(attributePaths = {"categoria", "formaFarmaceutica"})
    Optional<MedicamentosEntity> findById(Long id);

    /** Busca por nombre comercial o principio activo, sin distinguir mayusculas, trayendo la
     * formaFarmaceutica y la categoria de una sola vez. Basta con que coincida uno de los dos
     * campos. */
    @EntityGraph(attributePaths = {"formaFarmaceutica", "categoria"})
    List<MedicamentosEntity> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
            String nombreComercial, String principioActivo);

    /**
     * Busqueda combinada del buscador (HU-3). Es nativa porque necesita agrupar por
     * medicamento, calcular el precio minimo y contar farmacias aprobadas en la misma
     * consulta, cosas que no se expresan bien con consultas derivadas ni con JPQL.
     *
     * Los conteos solo miran farmacias con estado 'aprobada': las pendientes, suspendidas o
     * rechazadas no suman. Los filtros de farmacia y precio usan EXISTS para no alterar esos
     * conteos. Cuando vienen los dos, deben coincidir en la misma fila de disponibilidad.
     *
     * Los parametros numericos que empiezan con "buscar" o "sin" llegan como 1 o 0 porque
     * STOMP entrega los encabezados como texto y el controller ya los normalizo.
     */
    @Query(value = """
            SELECT
              m.id AS id,
              m.nombre_comercial AS nombreComercial,
              m.principio_activo AS principioActivo,
              c.nombre AS categoria,
              m.presentacion AS presentacion,
              ff.nombre AS formaFarmaceutica,
              MIN(CASE WHEN ef.nombre = 'aprobada' THEN d.precio END) AS precioMinimo,
              COUNT(DISTINCT CASE WHEN ef.nombre = 'aprobada' AND ed.nombre = 'disponible'
                                  THEN f.usuario_id END) AS farmaciasDisponibles,
              COUNT(DISTINCT CASE WHEN ef.nombre = 'aprobada' THEN f.usuario_id END)
                                  AS farmaciasReportaron
            FROM medicamentos m
            JOIN categorias c ON c.id = m.categoria_id
            JOIN formas_farmaceuticas ff ON ff.id = m.forma_farmaceutica_id
            LEFT JOIN disponibilidad d ON d.medicamento_id = m.id
            LEFT JOIN farmacias f ON f.usuario_id = d.farmacia_id
            LEFT JOIN estados_farmacia ef ON ef.id = f.estado_aprobacion_id
            LEFT JOIN estados_disponibilidad ed ON ed.id = d.estado_disponibilidad_id
            WHERE (:texto = ''
                   OR (:buscarNombre = 1 AND LOCATE(LOWER(:texto), LOWER(m.nombre_comercial)) > 0)
                   OR (:buscarPrincipio = 1 AND LOCATE(LOWER(:texto), LOWER(m.principio_activo)) > 0))
              AND (:farmacia = ''
                   OR EXISTS (
                     SELECT 1
                     FROM disponibilidad df
                     JOIN farmacias ff2 ON ff2.usuario_id = df.farmacia_id
                     JOIN estados_farmacia eff ON eff.id = ff2.estado_aprobacion_id
                     WHERE df.medicamento_id = m.id
                       AND eff.nombre = 'aprobada'
                       AND LOCATE(LOWER(:farmacia), LOWER(ff2.nombre_farmacia)) > 0
                       AND (:sinPrecio = 1
                            OR (df.precio >= :precioMin AND df.precio <= :precioMax))
                   ))
              AND (:sinPrecio = 1
                   OR EXISTS (
                     SELECT 1
                     FROM disponibilidad dp
                     JOIN farmacias fp ON fp.usuario_id = dp.farmacia_id
                     JOIN estados_farmacia efp ON efp.id = fp.estado_aprobacion_id
                     WHERE dp.medicamento_id = m.id
                       AND efp.nombre = 'aprobada'
                       AND dp.precio >= :precioMin AND dp.precio <= :precioMax
                   ))
            GROUP BY m.id, m.nombre_comercial, m.principio_activo, c.nombre, m.presentacion, ff.nombre
            ORDER BY
              CASE WHEN :orden = 'precio'
                   THEN COALESCE(MIN(CASE WHEN ef.nombre = 'aprobada' THEN d.precio END), 999999999)
              END ASC,
              CASE WHEN :orden = 'relevancia' AND :texto <> ''
                   THEN CASE
                     WHEN :buscarNombre = 1
                          AND LOCATE(LOWER(:texto), LOWER(m.nombre_comercial)) = 1 THEN 0
                     WHEN :buscarNombre = 1
                          AND LOCATE(LOWER(:texto), LOWER(m.nombre_comercial)) > 0 THEN 1
                     WHEN :buscarPrincipio = 1
                          AND LOCATE(LOWER(:texto), LOWER(m.principio_activo)) = 1 THEN 2
                     ELSE 3
                   END
              END ASC,
              m.nombre_comercial ASC
            """, countQuery = """
            SELECT COUNT(*)
            FROM medicamentos m
            WHERE (:texto = ''
                   OR (:buscarNombre = 1 AND LOCATE(LOWER(:texto), LOWER(m.nombre_comercial)) > 0)
                   OR (:buscarPrincipio = 1 AND LOCATE(LOWER(:texto), LOWER(m.principio_activo)) > 0))
              AND (:farmacia = ''
                   OR EXISTS (
                     SELECT 1
                     FROM disponibilidad df
                     JOIN farmacias ff2 ON ff2.usuario_id = df.farmacia_id
                     JOIN estados_farmacia eff ON eff.id = ff2.estado_aprobacion_id
                     WHERE df.medicamento_id = m.id
                       AND eff.nombre = 'aprobada'
                       AND LOCATE(LOWER(:farmacia), LOWER(ff2.nombre_farmacia)) > 0
                       AND (:sinPrecio = 1
                            OR (df.precio >= :precioMin AND df.precio <= :precioMax))
                   ))
              AND (:sinPrecio = 1
                   OR EXISTS (
                     SELECT 1
                     FROM disponibilidad dp
                     JOIN farmacias fp ON fp.usuario_id = dp.farmacia_id
                     JOIN estados_farmacia efp ON efp.id = fp.estado_aprobacion_id
                     WHERE dp.medicamento_id = m.id
                       AND efp.nombre = 'aprobada'
                       AND dp.precio >= :precioMin AND dp.precio <= :precioMax
                   ))
            """, nativeQuery = true)
    Page<MedicamentoBusquedaProyeccion> buscarMedicamentos(@Param("texto") String texto,
            @Param("buscarNombre") int buscarNombre, @Param("buscarPrincipio") int buscarPrincipio,
            @Param("farmacia") String farmacia, @Param("sinPrecio") int sinPrecio,
            @Param("precioMin") BigDecimal precioMin, @Param("precioMax") BigDecimal precioMax,
            @Param("orden") String orden, Pageable pageable);

    // --- Reporte manual de la farmacia (HU-7) ---

    /**
     * Crea el medicamento solo si esa combinación no existe. El "id = id" no cambia nada,
     * solo evita el error por duplicado. No se usa INSERT IGNORE porque taparía también fallos
     * de llave foránea y de CHECK.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT INTO medicamentos
              (nombre_comercial, principio_activo, categoria_id, presentacion, forma_farmaceutica_id)
            VALUES (:nombre, :principio, :categoriaId, :presentacion, :formaId)
            ON DUPLICATE KEY UPDATE id = id
            """, nativeQuery = true)
    int crearSiNoExiste(@Param("nombre") String nombre, @Param("principio") String principio,
            @Param("categoriaId") Integer categoriaId, @Param("presentacion") String presentacion,
            @Param("formaId") Integer formaId);

    /** Id del medicamento con esa combinación única; vacío si no existe. */
    @Query(value = """
            SELECT id FROM medicamentos
            WHERE nombre_comercial = :nombre
              AND principio_activo = :principio
              AND presentacion = :presentacion
              AND forma_farmaceutica_id = :formaId
            """, nativeQuery = true)
    Optional<Long> buscarIdPorCombinacion(@Param("nombre") String nombre,
            @Param("principio") String principio, @Param("presentacion") String presentacion,
            @Param("formaId") Integer formaId);
}
