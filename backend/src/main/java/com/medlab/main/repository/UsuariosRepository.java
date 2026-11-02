package com.medlab.main.repository;

import java.util.Optional;

import com.medlab.main.entity.UsuariosEntity;

/** Cuentas de pacientes y farmacias. */
public interface UsuariosRepository {

  /** Dice si ya existe una cuenta con esa cedula o UUID. */
  boolean existsByIdentificacion(String identificacion);

  /** Busca la cuenta por la identificación con la que inicia sesión el usuario. */
  Optional<UsuariosEntity> findByIdentificacion(String identificacion);

  /** Crea o actualiza una cuenta y confirma el cambio en la base. */
  UsuariosEntity save(UsuariosEntity usuariosEntity);
}
