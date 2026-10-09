package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.entity.Medicamento;
import com.medlab.main.repository.MedicamentoRepository;

import lombok.RequiredArgsConstructor;

/**
 * 
 * este archivo es  el service de los medicamentos, resuelve las
 * búsquedas del buscador de la landing
 * 
 */
@Service
@RequiredArgsConstructor
public class MedicamentoService {

	private final MedicamentoRepository medicamentoRepository;

	/**
	 * 
	 * devuelve los medicamentos cuyo nombre comercial o principio activo 
	 * contiene el texto que digitó el user
	 * 
	 * le pasa el mismo texto al repositorio dos veces, una para buscar en 
	 * el nombre comercial y otra para el principio activo. Después 
	 * convierte cada medicamento encontrado en un MedicamentoBusquedaDto y
	 * los junta en una lista
	 * 
	 */
	public List<MedicamentoBusquedaDto> buscar(String texto) {
		return medicamentoRepository
				.findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(texto, texto).stream()
				.map(this::aBusquedaDto).toList();
	}

	/** 
	 * 
	 * Qué hace: convierte un medicamento en los datos que muestra cada resultado
	 * del buscador: id, nombre comercial, principio activo, presentación y forma
	 * farmacéutica.
	 * 
	 * crea un MedicamentoBusquedaDto con esos datos. De la forma farmacéutica solo
	 * toma el nombre (por ejemplo "Tableta"), no el objeto completo.
	 * Para entenderlo: getFormaFarmaceutica().getNombre() funciona sin una consulta
	 * extra gracias al @EntityGraph del repositorio, que ya la trajo junto con el
	 * medicamento.
	 * 
	*/
	private MedicamentoBusquedaDto aBusquedaDto(Medicamento medicamento) {
		return new MedicamentoBusquedaDto(medicamento.getId(), medicamento.getNombreComercial(),
				medicamento.getPrincipioActivo(), medicamento.getPresentacion(),
				medicamento.getFormaFarmaceutica().getNombre());
	}
}
