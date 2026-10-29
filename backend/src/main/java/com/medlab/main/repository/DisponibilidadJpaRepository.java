package com.medlab.main.repository;

import com.medlab.main.entity.DisponibilidadEntity;
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
public interface DisponibilidadJpaRepository extends JpaRepository<DisponibilidadEntity, Integer> {

    List<DisponibilidadEntity> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    List<DisponibilidadEntity> findByFarmacia_NombreFarmaciaAndFarmacia_EstadoAprobacion_Nombre(
            String nombreFarmacia, String nombreEstado);

    /** Trae los reportes de un medicamento, del mas reciente al mas antiguo, trayendo la
     * farmacia en la misma consulta para mostrar su nombre sin consultas extra. */
    @EntityGraph(attributePaths = {"farmacia"})
    List<DisponibilidadEntity> findByMedicamento_IdOrderByFechaHoraReporteDesc(Long medicamentoId);

    // --- Panel de la farmacia (HU-11 y HU-7) ---

    /**
     * Productos de una farmacia, filtrados por texto en el nombre comercial. Con texto vacío
     * devuelve todos. Pagina para que farmacias con inventario grande no carguen todo de una vez.
     */
    @Query(value = """
            select d from DisponibilidadEntity d
            join fetch d.medicamento m
            join fetch d.estadoDisponibilidad
            where d.farmacia.usuarioId = :farmaciaId
              and lower(m.nombreComercial) like lower(concat('%', :texto, '%'))
            """, countQuery = """
            select count(d) from DisponibilidadEntity d
            where d.farmacia.usuarioId = :farmaciaId
              and lower(d.medicamento.nombreComercial) like lower(concat('%', :texto, '%'))
            """)
    Page<DisponibilidadEntity> buscarPorFarmacia(@Param("farmaciaId") Integer farmaciaId,
            @Param("texto") String texto, Pageable pageable);

    /** Una fila concreta de una farmacia; vacío si esa farmacia no maneja el medicamento. */
    @EntityGraph(attributePaths = {"medicamento", "estadoDisponibilidad"})
    Optional<DisponibilidadEntity> findByFarmaciaUsuarioIdAndMedicamentoId(Integer farmaciaId,
            Long medicamentoId);

    /** Cuántos productos tiene la farmacia en cada estado (base del dashboard). */
    @Query("""
            select d.estadoDisponibilidad.nombre as estado, count(d) as total
            from DisponibilidadEntity d
            where d.farmacia.usuarioId = :farmaciaId
            group by d.estadoDisponibilidad.nombre
            """)
    List<ConteoEstado> contarPorEstado(@Param("farmaciaId") Integer farmaciaId);

    /**
     * Crea la fila o, si la farmacia ya reportó ese medicamento, la actualiza. Es el punto
     * único de escritura que comparten HU-11 y HU-7.
     * La fecha se fija a mano porque MariaDB no la renueva cuando se guardan los mismos valores.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT INTO disponibilidad
              (farmacia_id, medicamento_id, cantidad_disponible, precio, estado_disponibilidad_id, fecha_hora_reporte)
            VALUES (:farmaciaId, :medicamentoId, :cantidad, :precio, :estadoId, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
              cantidad_disponible = VALUES(cantidad_disponible),
              precio = VALUES(precio),
              estado_disponibilidad_id = VALUES(estado_disponibilidad_id),
              fecha_hora_reporte = CURRENT_TIMESTAMP
            """, nativeQuery = true)
    int guardar(@Param("farmaciaId") Integer farmaciaId, @Param("medicamentoId") Long medicamentoId,
            @Param("cantidad") Integer cantidad, @Param("precio") BigDecimal precio,
            @Param("estadoId") Integer estadoId);

    /**
     * Baja lógica: deja el producto en cero y agotado, pero conserva la fila y su precio
     * para no perder el histórico. Devuelve 0 si la farmacia no tenía ese producto.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE disponibilidad
            SET cantidad_disponible = 0, estado_disponibilidad_id = :estadoAgotadoId,
                fecha_hora_reporte = CURRENT_TIMESTAMP
            WHERE farmacia_id = :farmaciaId AND medicamento_id = :medicamentoId
            """, nativeQuery = true)
    int marcarAgotado(@Param("farmaciaId") Integer farmaciaId, @Param("medicamentoId") Long medicamentoId,
            @Param("estadoAgotadoId") Integer estadoAgotadoId);

    /** Fila del conteo por estado: nombre del estado y cuántos productos tiene. */
    interface ConteoEstado {

        String getEstado();

        Long getTotal();
    }
}