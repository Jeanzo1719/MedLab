package com.medlab.main.repository;

import com.medlab.main.entity.AuditoriaEstadosFarmaciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaEstadosFarmaciaJpaRepository
    extends JpaRepository<AuditoriaEstadosFarmaciaEntity, Integer> {
}
