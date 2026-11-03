package com.medlab.main.dto;

/**
 * Respuesta del registro: el id de la cuenta, su identificación, su rol y su estado.
 *
 * La identificación es con la que la cuenta inicia sesión: la cédula del paciente o el código
 * (UUID) que el sistema le genera a la farmacia. Esta respuesta es la única vez que la farmacia lo
 * recibe, por eso la pantalla de registro se lo muestra.
 *
 * Una farmacia nace en "pendiente" porque el administrador todavía tiene que aprobarla; un
 * paciente nace "activo". El estado del paciente no se guarda en la base: se deduce de su rol.
 */
public record RegistroResponseDto(Integer id, String identificacion, String rol, String estado) {
}
