package com.medlab.main.repository.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.medlab.main.entity.SesionesEntity;
import com.medlab.main.repository.SesionesJpaRepository;
import com.medlab.main.repository.SesionesRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class SesionesRepositoryImpl implements SesionesRepository {

  private final SesionesJpaRepository jpaRepository;

  @Override
  public Optional<SesionesEntity> findByToken(String token) {
    return jpaRepository.findByToken(token);
  }

  @Override
  public SesionesEntity save(SesionesEntity sesionesEntity) {
    return jpaRepository.save(sesionesEntity);
  }

  @Override
  public void cerrarActivasDeUsuario(Integer usuarioId, LocalDateTime fechaCierre) {
    jpaRepository.cerrarActivasDeUsuario(usuarioId, fechaCierre);
  }

  @Override
  public void registrarActividad(Integer sesionId) {
    jpaRepository.registrarActividad(sesionId);
  }
}
