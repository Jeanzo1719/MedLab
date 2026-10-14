package com.medlab.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medlab.main.entity.Medicamento;

/** Acceso a la tabla medicamentos. */
public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

  /** Detalle de un medicamento con categoría y forma farmacéutica en una sola consulta. */
  @Query("""
      select m from Medicamento m
      join fetch m.categoria
      join fetch m.formaFarmaceutica
      where m.id = :id
      """)
  Optional<Medicamento> findDetalleById(@Param("id") Integer id);

  /**
   * Busca por nombre comercial o principio activo, sin distinguir mayúsculas
   * (ej. "ibu" encuentra "Ibuprofeno"). Trae la forma farmacéutica de una vez.
   */
  @EntityGraph(attributePaths = "formaFarmaceutica")
  List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
      String nombreComercial, String principioActivo);
}