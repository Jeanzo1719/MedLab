package com.medlab.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.AuditoriaEstadoFarmacia;

/** Historial de estados de las farmacias. */
public interface AuditoriaEstadoFarmaciaRepository
    extends JpaRepository<AuditoriaEstadoFarmacia, Integer> {

  /** Lo más nuevo primero; los dos estados se traen juntos para no hacer una consulta por fila. */
  @EntityGraph(attributePaths = {"estadoAnterior", "estadoNuevo"})
  Page<AuditoriaEstadoFarmacia> findByFarmaciaUsuarioIdOrderByFechaDescIdDesc(Integer farmaciaId,
      Pageable paginacion);
}