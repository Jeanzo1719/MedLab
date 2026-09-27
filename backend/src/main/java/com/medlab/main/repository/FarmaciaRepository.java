package com.medlab.main.repository;

import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.entity.Farmacia;
import java.util.List;

/**
 * Data access for pharmacies. The signature matches a Spring Data derived
 * query, so the service will not change when MongoDB is connected.
 */
public interface FarmaciaRepository {

  List<Farmacia> findByEstadoAprobacion(EstadoAprobacion estadoAprobacion);
}
