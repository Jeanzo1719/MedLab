package com.medlab.main.repository;

import com.medlab.main.entity.Disponibilidad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {

  @Query("""
      select d from Disponibilidad d
      join fetch d.farmacia
      where d.medicamento.id = :medicamentoId
      order by d.fechaHoraReporte desc
      """)
  List<Disponibilidad> findByMedicamentoIdConFarmacia(@Param("medicamentoId") Integer medicamentoId);
} 