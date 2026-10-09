package com.medlab.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.Medicamento;

/**
 * 
 * este archivo es un repositorio que da acceso a la tabla medicamentos
 * 
 * busca los medicamentos cuyo nombre comercial o principio activo contenga
 * el texto que escribió el usuario, sin importar mayúsculas ni minúsculas. 
 * Por ejemplo, con "ibu" encuentra "Ibuprofeno".
 * 
 */
public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

	/**
	 * 
	 * atributo que cuando trae los medicamentos, trae también su forma farmacéutica
	 * (tableta, jarabe…) en la misma consulta.
	 * 
	 * formaFarmaceutica normalmente se carga solo cuando se necesita
	 * (LAZY, como viste en las entidades). Esta anotación le dice a JPA que en esta
	 * búsqueda la traiga de una vez, junto con el medicamento.
	 * 
	 */
	@EntityGraph(attributePaths = "formaFarmaceutica")
	List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
			String nombreComercial, String principioActivo);
}
