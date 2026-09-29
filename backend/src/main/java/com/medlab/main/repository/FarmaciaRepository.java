package com.medlab.main.repository;

import java.util.List;

import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.entity.Farmacia;

/**
 * Contrato de acceso a datos de las farmacias (capa de repositorios).
 * <p>
 * Para qué sirve: los servicios dependen de esta interfaz y nunca de un almacenamiento concreto, así la fuente de datos
 * puede cambiar sin tocar la lógica de negocio.
 * <p>
 * Cómo funciona: la firma del método sigue la convención de consultas derivadas de Spring Data. Cuando se conecte
 * MongoDB, esta interfaz extenderá {@code MongoRepository} y Spring la implementará; mientras tanto la implementa
 * {@link FarmaciaRepositoryMock}. {@link com.medlab.main.service.FarmaciaService} no cambiará.
 */
public interface FarmaciaRepository {

	/** Farmacias en el estado de aprobación indicado; una lista vacía si no hay ninguna */
	List<Farmacia> findByEstadoAprobacion(EstadoAprobacion estadoAprobacion);
}
