package com.medlab.main.repository;

import com.medlab.main.entity.PacientesEntity;

/** Datos personales de los pacientes y cuidadores. */
public interface PacientesRepository {

  /** Guarda el perfil del paciente; su id es el de la cuenta recién creada. */
  PacientesEntity save(PacientesEntity pacientesEntity);
}
