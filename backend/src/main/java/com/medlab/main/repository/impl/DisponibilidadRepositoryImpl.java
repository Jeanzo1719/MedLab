package com.medlab.main.repository.impl;

import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.repository.DisponibilidadJpaRepository;
import com.medlab.main.repository.DisponibilidadRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class DisponibilidadRepositoryImpl implements DisponibilidadRepository {

  private static final String ESTADO_FARMACIA_APROBADA = "aprobada";

  private final DisponibilidadJpaRepository jpaRepository;

  @Override
  public List<DisponibilidadEntity> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax) {
    return jpaRepository.findByPrecioBetween(precioMin, precioMax);
  }

  @Override
  public List<MedicamentosEntity> findMedicamentosByNombreFarmaciaAprobada(String nombreFarmacia) {
    List<DisponibilidadEntity> disponibilidades =
        jpaRepository.findByFarmacia_NombreFarmaciaAndFarmacia_EstadoAprobacion_Nombre(
            nombreFarmacia, ESTADO_FARMACIA_APROBADA);

    // Se deduplica por id porque un mismo medicamento puede tener varias
    // filas de disponibilidad en la misma farmacia.
    Map<Long, MedicamentosEntity> medicamentos = new LinkedHashMap<>();
    for (DisponibilidadEntity disponibilidad : disponibilidades) {
      medicamentos.putIfAbsent(
          disponibilidad.getMedicamento().getId(), disponibilidad.getMedicamento());
    }

    return List.copyOf(medicamentos.values());
  }
}
