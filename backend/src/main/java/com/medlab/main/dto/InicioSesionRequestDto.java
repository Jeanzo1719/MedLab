package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Credenciales del inicio de sesión (HU-2).
 *
 * La identificación es la cédula del paciente/cuidador (6 a 10 dígitos) o el UUID de una
 * farmacia, que es como quedó registrada en HU-1. La contraseña se valida aquí antes de
 * compararla contra el hash: si no mide entre 8 y 25 caracteres no hace falta gastar Argon2.
 */
public record InicioSesionRequestDto(

    @NotBlank(message = "La identificación es obligatoria")
    @Pattern(
        regexp = "^(?:[0-9]{6,10}|[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$",
        message = "La identificación debe tener entre 6 y 10 dígitos")
    String identificacion,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 25, message = "La contraseña debe tener entre 8 y 25 caracteres")
    String contrasena) {
}
