package com.medlab.main.repository;

import java.util.List;

import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.entity.Farmacia;

/**
 * Data access for pharmacies. The signature matches a Spring Data derived query, so the service will not change when
 * MongoDB is connected.
 */
public interface FarmaciaRepository {

	List<Farmacia> findByEstadoAprobacion(EstadoAprobacion estadoAprobacion);
}
