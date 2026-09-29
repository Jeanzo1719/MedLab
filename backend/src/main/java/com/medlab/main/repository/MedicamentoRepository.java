package com.medlab.main.repository;

import java.util.List;

import com.medlab.main.entity.Medicamento;

/**
 * Data access for medicines. The signature matches a Spring Data derived query, so the service will not change when
 * MongoDB is connected.
 */
public interface MedicamentoRepository {

	List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
			String nombreComercial, String principioActivo);
}
