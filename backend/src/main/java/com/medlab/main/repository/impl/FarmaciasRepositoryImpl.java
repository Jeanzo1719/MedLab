package com.medlab.main.repository.impl;

import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.repository.FarmaciasJpaRepository;
import com.medlab.main.repository.FarmaciasRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FarmaciasRepositoryImpl implements FarmaciasRepository {

  private final FarmaciasJpaRepository jpaRepository;

  @Override
  public List<FarmaciasEntity> findByEstadoAprobacionNombre(String nombreEstado) {
    return jpaRepository.findByEstadoAprobacionNombre(nombreEstado);
  }

  @Override
  public List<FarmaciaCercana> findFarmaciasCercanasConDisponibilidad(String estadoFarmacia,
      String estadoExcluido, Long medicamentoId, double latitud, double longitud,
      double radioKm, int limite) {
    return jpaRepository.findFarmaciasCercanasConDisponibilidad(
        estadoFarmacia, estadoExcluido, medicamentoId, latitud, longitud, radioKm, limite);
  }
}
