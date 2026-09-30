package com.medlab.main.repository;

import java.util.List;

import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.entity.Farmacia;

public interface FarmaciaRepository {

	List<Farmacia> findByEstadoAprobacion(EstadoAprobacion estadoAprobacion);
}
