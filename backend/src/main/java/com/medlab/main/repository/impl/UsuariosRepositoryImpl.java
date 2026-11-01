package com.medlab.main.repository.impl;

import org.springframework.stereotype.Repository;

import com.medlab.main.entity.UsuariosEntity;
import com.medlab.main.repository.UsuariosJpaRepository;
import com.medlab.main.repository.UsuariosRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class UsuariosRepositoryImpl implements UsuariosRepository {

  private final UsuariosJpaRepository jpaRepository;

  @Override
  public boolean existsByIdentificacion(String identificacion) {
    return jpaRepository.existsByIdentificacion(identificacion);
  }

  /**
   * saveAndFlush y no save: así la base revisa la cedula unica dentro de la transaccion y el
   * registro puede responder "ya existe" en el momento, no al cerrar la transaccion.
   */
  @Override
  public UsuariosEntity save(UsuariosEntity usuariosEntity) {
    return jpaRepository.saveAndFlush(usuariosEntity);
  }
}
