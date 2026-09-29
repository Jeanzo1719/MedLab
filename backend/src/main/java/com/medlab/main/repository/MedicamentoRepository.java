package com.medlab.main.repository;

import java.util.List;

import com.medlab.main.entity.Medicamento;

/**
 * Contrato de acceso a datos de los medicamentos (capa de repositorios).
 * <p>
 * Para qué sirve: los servicios dependen de esta interfaz y nunca de un almacenamiento concreto, así la fuente de datos
 * puede cambiar sin tocar la lógica de negocio.
 * <p>
 * Cómo funciona: la firma del método sigue la convención de consultas derivadas de Spring Data. Cuando se conecte
 * MongoDB, esta interfaz extenderá {@code MongoRepository} y Spring la implementará; mientras tanto la implementa
 * {@link MedicamentoRepositoryMock}. {@link com.medlab.main.service.MedicamentoService} no cambiará.
 */
public interface MedicamentoRepository {

	/**
	 * Medicamentos cuyo nombre comercial contiene {@code nombreComercial} o cuyo principio activo contiene
	 * {@code principioActivo}, sin distinguir mayúsculas; una lista vacía si no hay coincidencias.
	 */
	List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
			String nombreComercial, String principioActivo);
}
