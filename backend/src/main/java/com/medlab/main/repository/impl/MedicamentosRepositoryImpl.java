package com.medlab.main.repository.impl;

import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.repository.MedicamentosJpaRepository;
import com.medlab.main.repository.MedicamentosRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MedicamentosRepositoryImpl implements MedicamentosRepository {

  private final MedicamentosJpaRepository jpaRepository;

  @Override
  public List<MedicamentosEntity> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  public Page<MedicamentosEntity> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable);
  }

  @Override
  public Optional<MedicamentosEntity> findById(Long id) {
    return jpaRepository.findById(id);
  }

  @Override
  public MedicamentosEntity save(MedicamentosEntity medicamentosEntity) {
    return jpaRepository.save(medicamentosEntity);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public Optional<MedicamentosEntity> findByNombreComercial(String nombreComercial) {
    return jpaRepository.findByNombreComercial(nombreComercial);
  }

  @Override
  public Optional<MedicamentosEntity> findByPrincipioActivo(String principioActivo) {
    return jpaRepository.findByPrincipioActivo(principioActivo);
  }

  @Override
  public List<MedicamentosEntity> findByPrincipioActivoAndIdNot(String principioActivo, Long id) {
    return jpaRepository.findByPrincipioActivoAndIdNot(principioActivo, id);
  }
}