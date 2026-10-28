package com.medlab.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medlab.main.entity.AlertaInactividadFarmacia;

/** Alertas de inactividad. Las consultas del job y del panel se agregan en los pasos 2 y 3. */
public interface AlertaInactividadFarmaciaRepository
    extends JpaRepository<AlertaInactividadFarmacia, Integer> {
}