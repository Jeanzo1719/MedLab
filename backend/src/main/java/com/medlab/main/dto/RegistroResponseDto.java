package com.medlab.main.dto;

/**
 * Respuesta del registro: el id de la cuenta, su rol y su estado.
 *
 * Una farmacia nace en "pendiente" porque el administrador todavía tiene que aprobarla; un
 * paciente nace "activo". El estado del paciente no se guarda en la base: se deduce de su rol.
 */
public record RegistroResponseDto(Integer id, String rol, String estado) {
}
