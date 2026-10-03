package com.medlab.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.Farmacia;

public interface FarmaciaRepository extends JpaRepository<Farmacia, Integer> {

	List<Farmacia> findByEstadoAprobacionNombre(String nombre);
}
