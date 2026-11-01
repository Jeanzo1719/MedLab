package com.medlab.main.repository.impl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.medlab.main.entity.RolesEntity;
import com.medlab.main.repository.RolesJpaRepository;
import com.medlab.main.repository.RolesRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class RolesRepositoryImpl implements RolesRepository {

  private final RolesJpaRepository jpaRepository;

  @Override
  public Optional<RolesEntity> findByNombre(String nombre) {
    return jpaRepository.findByNombre(nombre);
  }
}
