package com.medlab.main.service;

import com.medlab.main.dto.RegistroFarmaciaRequestDto;
import com.medlab.main.dto.RegistroPacienteRequestDto;
import com.medlab.main.dto.RegistroResponseDto;

/**
 * Crea las cuentas de pacientes/cuidadores y de farmacias (HU-1).
 */
public interface RegistroService {

  String ROL_PACIENTE = "paciente";

  String ROL_FARMACIA = "farmacia";

  /** Una farmacia nace esperando la aprobación del administrador. */
  String ESTADO_PENDIENTE = "pendiente";

  /** El paciente puede usar la cuenta de inmediato. */
  String ESTADO_ACTIVO = "activo";

  /** Registra un paciente/cuidador con su cédula y sus datos personales. */
  RegistroResponseDto registrarPaciente(RegistroPacienteRequestDto datos);

  /** Registra una farmacia, la deja pendiente y le genera un UUID como identificación. */
  RegistroResponseDto registrarFarmacia(RegistroFarmaciaRequestDto datos);
}
