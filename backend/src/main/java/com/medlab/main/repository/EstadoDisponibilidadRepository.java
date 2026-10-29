package com.medlab.main.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.EstadoDisponibilidad;

/** Busca un estado por su nombre ("disponible", "agotado", "bajo_pedido"). */
public interface EstadoDisponibilidadRepository extends JpaRepository<EstadoDisponibilidad, Integer> {

  Optional<EstadoDisponibilidad> findByNombre(String nombre);
}