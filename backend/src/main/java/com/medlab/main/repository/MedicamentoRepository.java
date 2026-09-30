package com.medlab.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.Medicamento;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

	@EntityGraph(attributePaths = "formaFarmaceutica")
	List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
			String nombreComercial, String principioActivo);
}
