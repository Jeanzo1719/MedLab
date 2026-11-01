package com.medlab.main.repository.impl;

import org.springframework.stereotype.Repository;

import com.medlab.main.entity.PacientesEntity;
import com.medlab.main.repository.PacientesJpaRepository;
import com.medlab.main.repository.PacientesRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class PacientesRepositoryImpl implements PacientesRepository {

  private final PacientesJpaRepository jpaRepository;

  @Override
  public PacientesEntity save(PacientesEntity pacientesEntity) {
    return jpaRepository.save(pacientesEntity);
  }
}
