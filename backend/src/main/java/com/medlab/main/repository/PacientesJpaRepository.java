package com.medlab.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medlab.main.entity.PacientesEntity;

@Repository
public interface PacientesJpaRepository extends JpaRepository<PacientesEntity, Integer> {
}
