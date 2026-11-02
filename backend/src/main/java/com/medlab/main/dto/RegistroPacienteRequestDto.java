package com.medlab.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario de registro cuando el rol es paciente/cuidador.
 *
 * El correo se valida con una sola arroba, sin espacios, con la parte anterior limitada a
 * letras, números y . _ - +, y la posterior con formato nombre.extensión (extensión de 2 a 10
 * letras). La contraseña debe tener entre 8 y 25 caracteres, al menos una mayúscula, una
 * minúscula y un símbolo. El segundo nombre es opcional: si la persona tiene tres o más
 * nombres, aquí se escriben todos los adicionales (máximo 100 caracteres).
 */
public record RegistroPacienteRequestDto(

    @NotBlank(message = "La identificación es obligatoria")
    @Pattern(regexp = "^\\d{6,10}$", message = "La identificación debe tener entre 6 y 10 dígitos")
    String identificacion,

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 150, message = "El correo no puede pasar de 150 caracteres")
    @Pattern(
        regexp = "^[A-Za-z0-9._+-]+@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z]{2,10}$",
        message = "El correo no tiene un formato válido")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 25, message = "La contraseña debe tener entre 8 y 25 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,25}$",
        message = "La contraseña debe tener al menos una mayúscula, una minúscula y un símbolo")
    String contrasena,

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20, message = "El teléfono no puede pasar de 20 caracteres")
    @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "El teléfono solo admite números, espacios y los signos + ( ) -")
    String telefono,

    @NotBlank(message = "La EPS es obligatoria")
    @Size(max = 100, message = "La EPS no puede pasar de 100 caracteres")
    String epsAfiliada,

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 50, message = "El primer nombre no puede pasar de 50 caracteres")
    String primerNombre,

    @Size(max = 100, message = "El segundo nombre no puede pasar de 100 caracteres")
    String segundoNombre,

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden pasar de 100 caracteres")
    String apellidos) {
}
