package com.medlab.main.repository;

import java.util.Optional;

import com.medlab.main.entity.RolesEntity;

/** Roles disponibles: paciente, farmacia y administrador. */
public interface RolesRepository {

  /** Busca un rol por su nombre, por ejemplo "paciente". */
  Optional<RolesEntity> findByNombre(String nombre);
}
