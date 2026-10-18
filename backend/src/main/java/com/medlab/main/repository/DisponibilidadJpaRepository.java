package com.medlab.main.repository;

import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.MedicamentosEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadJpaRepository extends JpaRepository<DisponibilidadEntity, Integer> {

    List<DisponibilidadEntity> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    @Query("""
            SELECT DISTINCT d.medicamento
            FROM DisponibilidadEntity d
            JOIN d.farmacia f
            JOIN f.estadoAprobacion e
            WHERE f.nombreFarmacia = :nombreFarmacia
              AND e.nombre = 'aprobada'
            """)
    List<MedicamentosEntity> findMedicamentosByNombreFarmaciaAprobada(
            @Param("nombreFarmacia") String nombreFarmacia);
}
