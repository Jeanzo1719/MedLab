package com.medlab.main.repository;

import com.medlab.main.entity.DisponibilidadEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
