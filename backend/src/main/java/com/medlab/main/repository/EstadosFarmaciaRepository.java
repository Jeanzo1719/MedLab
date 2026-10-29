package com.medlab.main.repository;

import com.medlab.main.entity.EstadosFarmaciaEntity;
import java.util.Optional;

public interface EstadosFarmaciaRepository {

  /** Busca un estado de farmacia por su nombre, por ejemplo "aprobada". */
  Optional<EstadosFarmaciaEntity> findByNombre(String nombre);
}
