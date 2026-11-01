package com.medlab.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medlab.main.entity.RolesEntity;

@Repository
public interface RolesJpaRepository extends JpaRepository<RolesEntity, Integer> {

  Optional<RolesEntity> findByNombre(String nombre);
}
