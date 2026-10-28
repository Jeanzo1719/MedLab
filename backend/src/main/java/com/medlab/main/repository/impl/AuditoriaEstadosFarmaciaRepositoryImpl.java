package com.medlab.main.repository.impl;

import com.medlab.main.entity.AuditoriaEstadosFarmaciaEntity;
import com.medlab.main.repository.AuditoriaEstadosFarmaciaJpaRepository;
import com.medlab.main.repository.AuditoriaEstadosFarmaciaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class AuditoriaEstadosFarmaciaRepositoryImpl implements AuditoriaEstadosFarmaciaRepository {

  private final AuditoriaEstadosFarmaciaJpaRepository jpaRepository;

  @Override
  public AuditoriaEstadosFarmaciaEntity save(
      AuditoriaEstadosFarmaciaEntity auditoriaEstadosFarmaciaEntity) {
    return jpaRepository.save(auditoriaEstadosFarmaciaEntity);
  }
}
