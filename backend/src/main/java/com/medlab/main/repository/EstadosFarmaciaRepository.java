package com.medlab.main.repository;

import com.medlab.main.entity.EstadosFarmaciaEntity;
import java.util.Optional;

public interface EstadosFarmaciaRepository {

  Optional<EstadosFarmaciaEntity> findByNombre(String nombre);
}
