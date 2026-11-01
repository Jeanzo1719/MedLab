package com.medlab.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medlab.main.entity.UsuariosEntity;

@Repository
public interface UsuariosJpaRepository extends JpaRepository<UsuariosEntity, Integer> {

  boolean existsByIdentificacion(String identificacion);
}
