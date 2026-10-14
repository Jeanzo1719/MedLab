package com.medlab.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.Farmacia;

/**
 * 
 * este es el repositorio que da acceso a la tabla farmacias
 * 
 * devuelve la lista de farmacias que están en el estado que se le solicita
 * 
 */
public interface FarmaciaRepository extends JpaRepository<Farmacia, Integer> {

	List<Farmacia> findByEstadoAprobacionNombre(String nombre);
}
