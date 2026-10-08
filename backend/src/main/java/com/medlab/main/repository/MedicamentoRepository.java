package com.medlab.main.repository;

import com.medlab.main.entity.Medicamento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

  @Query("""
      select m from Medicamento m
      join fetch m.categoria
      join fetch m.formaFarmaceutica
      where m.id = :id
      """)
  Optional<Medicamento> findDetalleById(@Param("id") Integer id);
}