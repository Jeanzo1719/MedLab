package com.medlab.main.repository.impl;

import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.repository.DisponibilidadJpaRepository;
import com.medlab.main.repository.DisponibilidadRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class DisponibilidadRepositoryImpl implements DisponibilidadRepository {

  private final DisponibilidadJpaRepository jpaRepository;

  @Override
  public List<DisponibilidadEntity> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax) {
    return jpaRepository.findByPrecioBetween(precioMin, precioMax);
  }

  @Override
  public List<MedicamentosEntity> findMedicamentosByNombreFarmaciaAprobada(String nombreFarmacia) {
    return jpaRepository.findMedicamentosByNombreFarmaciaAprobada(nombreFarmacia);
  }
}
