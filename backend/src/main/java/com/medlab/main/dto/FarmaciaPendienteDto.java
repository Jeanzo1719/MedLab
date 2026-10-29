package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 
 * los datos de una farmacia que espera aprobación, los que el administrador
 * revisa para decidir si la aprueba o la rechaza
 * 
 * id: el número que identifica a la farmacia (el id de su usuario). Es obligatorio
 * nombre: el nombre de la farmacia. No puede estar vacío y tiene un máximo de 150
 * caracteres
 * nit: el número de identificación tributaria. Máximo 20 caracteres
 * rut: el registro único tributario. Máximo 20 caracteres
 * matriculaMercantil: la matrícula de la cámara de comercio. Máximo 20 caracteres
 * identificacionDirectorTecnico: el documento del director técnico de la farmacia.
 * Máximo 10 caracteres
 * licenciaSanitaria: la licencia que le permite funcionar. Máximo 50 caracteres
 * 
 * los cinco últimos son los datos de validación que la farmacia dio al
 * registrarse (HU-1). Todos son obligatorios y sus máximos son los de la tabla
 * farmacias
 * 
 */
public record FarmaciaPendienteDto(@NotNull Integer id, @NotBlank @Size(max = 150) String nombre,
		@NotBlank @Size(max = 20) String nit, @NotBlank @Size(max = 20) String rut,
		@NotBlank @Size(max = 20) String matriculaMercantil,
		@NotBlank @Size(max = 10) String identificacionDirectorTecnico,
		@NotBlank @Size(max = 50) String licenciaSanitaria) {
}
