package com.medlab.main.repository;

import com.medlab.main.entity.EstadosFarmaciaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadosFarmaciaJpaRepository extends JpaRepository<EstadosFarmaciaEntity, Integer> {

    Optional<EstadosFarmaciaEntity> findByNombre(String nombre);
}
