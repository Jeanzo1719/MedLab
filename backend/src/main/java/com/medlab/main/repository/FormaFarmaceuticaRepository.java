package com.medlab.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.FormaFarmaceutica;

public interface FormaFarmaceuticaRepository extends JpaRepository<FormaFarmaceutica, Integer> {

  List<FormaFarmaceutica> findAllByOrderByNombreAsc();
}