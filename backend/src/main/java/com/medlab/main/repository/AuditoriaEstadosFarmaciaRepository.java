package com.medlab.main.repository;

import com.medlab.main.entity.AuditoriaEstadosFarmaciaEntity;

public interface AuditoriaEstadosFarmaciaRepository {

  /** Guarda un registro de cambio de estado de una farmacia. */
  AuditoriaEstadosFarmaciaEntity save(AuditoriaEstadosFarmaciaEntity auditoriaEstadosFarmaciaEntity);
}
