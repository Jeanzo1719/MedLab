package com.medlab.main.repository.impl;

import com.medlab.main.entity.EstadosFarmaciaEntity;
import com.medlab.main.repository.EstadosFarmaciaJpaRepository;
import com.medlab.main.repository.EstadosFarmaciaRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class EstadosFarmaciaRepositoryImpl implements EstadosFarmaciaRepository {

  private final EstadosFarmaciaJpaRepository jpaRepository;

  @Override
  public Optional<EstadosFarmaciaEntity> findByNombre(String nombre) {
    return jpaRepository.findByNombre(nombre);
  }
}
