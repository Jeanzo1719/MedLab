package com.medlab.main.repository;

import com.medlab.main.entity.MedicamentosEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentosRepository extends JpaRepository<MedicamentosEntity, Long> {

  List<MedicamentosEntity> findByNombreComercial(String nombreComercial);

  List<MedicamentosEntity> findByPrincipioActivo(String principioActivo);
}