package com.medlab.main.repository;

import java.util.List;

import com.medlab.main.entity.Medicamento;

public interface MedicamentoRepository {

	List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
			String nombreComercial, String principioActivo);
}
