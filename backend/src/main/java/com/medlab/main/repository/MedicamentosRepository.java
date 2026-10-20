package com.medlab.main.repository;

import com.medlab.main.entity.MedicamentosEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicamentosRepository {

  List<MedicamentosEntity> findAll();

  Page<MedicamentosEntity> findAll(Pageable pageable);

  Optional<MedicamentosEntity> findById(Long id);

  MedicamentosEntity save(MedicamentosEntity medicamentosEntity);

  void deleteById(Long id);

  Optional<MedicamentosEntity> findByNombreComercial(String nombreComercial);

  Optional<MedicamentosEntity> findByPrincipioActivo(String principioActivo);

  List<MedicamentosEntity> findByPrincipioActivoAndIdNot(String principioActivo, Long id);

  List<MedicamentosEntity> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
      String nombreComercial, String principioActivo);
}