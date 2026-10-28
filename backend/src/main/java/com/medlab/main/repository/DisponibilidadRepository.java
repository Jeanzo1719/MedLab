package com.medlab.main.repository;

import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.MedicamentosEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadRepository {

  List<DisponibilidadEntity> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

  List<MedicamentosEntity> findMedicamentosByNombreFarmaciaAprobada(String nombreFarmacia);

  List<DisponibilidadEntity> findByMedicamentoIdOrderByFechaHoraReporteDesc(Long medicamentoId);

  Optional<DisponibilidadEntity> findById(Integer disponibilidadId);
}
