package com.medlab.main.repository;

import com.medlab.main.entity.MedicamentosEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentosJpaRepository extends JpaRepository<MedicamentosEntity, Long> {

    Optional<MedicamentosEntity> findByNombreComercial(String nombreComercial);

    Optional<MedicamentosEntity> findByPrincipioActivo(String principioActivo);

    List<MedicamentosEntity> findByPrincipioActivoAndIdNot(String principioActivo, Long id);
}
