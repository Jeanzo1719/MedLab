package com.medlab.main.repository.impl;

import com.medlab.main.repository.CalificacionesJpaRepository;
import com.medlab.main.repository.CalificacionesRepository;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CalificacionesRepositoryImpl implements CalificacionesRepository {

  private final CalificacionesJpaRepository jpaRepository;

  @Override
  public List<CalificacionReciente> findCalificacionesRecientesByDisponibilidad(
      Collection<Integer> disponibilidadIds, int limite) {
    return jpaRepository.findCalificacionesRecientesByDisponibilidad(disponibilidadIds, limite);
  }

  @Override
  public int saveCalificacion(Integer disponibilidadId, Integer usuarioId,
      Integer tipoCalificacionId) {
    return jpaRepository.upsertCalificacion(disponibilidadId, usuarioId, tipoCalificacionId);
  }
}
