package com.medlab.main.repository;

import com.medlab.main.entity.Medicamento;
import java.util.List;

/**
 * Data access for medicines. The signature matches a Spring Data derived
 * query, so the service will not change when MongoDB is connected.
 */
public interface MedicamentoRepository {

  List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
      String nombreComercial, String principioActivo);
}
