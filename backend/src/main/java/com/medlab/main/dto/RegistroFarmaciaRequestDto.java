package com.medlab.main.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario de registro cuando el rol es farmacia.
 *
 * La dirección se pide por partes, igual que la guarda la tabla farmacias: tipo de vía, número,
 * letra opcional, vía generadora, letra opcional, número del predio y complemento opcional.
 * La matrícula mercantil solo admite letras y números (5 a 20 caracteres); la identificación del
 * director técnico, entre 6 y 10 dígitos. La latitud y la longitud llegan del selector de
 * ubicación del formulario.
 */
public record RegistroFarmaciaRequestDto(

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

    @NotBlank(message = "El nombre de la farmacia es obligatorio")
    @Size(max = 150, message = "El nombre de la farmacia no puede pasar de 150 caracteres")
    String nombreFarmacia,

    @NotBlank(message = "El tipo de vía es obligatorio")
    @Size(max = 20, message = "El tipo de vía no puede pasar de 20 caracteres")
    String tipoViaPrincipal,

    @NotBlank(message = "El número de la vía principal es obligatorio")
    @Size(max = 10, message = "El número de la vía principal no puede pasar de 10 caracteres")
    String numeroViaPrincipal,

    @Size(max = 5, message = "La letra de la vía principal no puede pasar de 5 caracteres")
    String letraViaPrincipal,

    @NotBlank(message = "El número de la vía generadora es obligatorio")
    @Size(max = 10, message = "El número de la vía generadora no puede pasar de 10 caracteres")
    String numeroViaGeneradora,

    @Size(max = 5, message = "La letra de la vía generadora no puede pasar de 5 caracteres")
    String letraViaGeneradora,

    @NotBlank(message = "El número del predio es obligatorio")
    @Size(max = 10, message = "El número del predio no puede pasar de 10 caracteres")
    String numeroPredio,

    @Size(max = 100, message = "El complemento no puede pasar de 100 caracteres")
    String complemento,

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 20, message = "El NIT no puede pasar de 20 caracteres")
    String nit,

    @NotBlank(message = "El RUT es obligatorio")
    @Size(max = 20, message = "El RUT no puede pasar de 20 caracteres")
    String rut,

    @NotBlank(message = "La matrícula mercantil es obligatoria")
    @Pattern(regexp = "^[A-Za-z0-9]{5,20}$",
        message = "La matrícula mercantil debe tener entre 5 y 20 letras o números, sin espacios ni símbolos")
    String matriculaMercantil,

    @NotBlank(message = "La identificación del director técnico es obligatoria")
    @Pattern(regexp = "^\\d{6,10}$",
        message = "La identificación del director técnico debe tener entre 6 y 10 dígitos")
    String identificacionDirectorTecnico,

    @NotBlank(message = "La licencia sanitaria es obligatoria")
    @Size(max = 50, message = "La licencia sanitaria no puede pasar de 50 caracteres")
    String licenciaSanitaria,

    @NotNull(message = "Debes marcar la ubicación de la farmacia en el mapa")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    Double latitud,

    @NotNull(message = "Debes marcar la ubicación de la farmacia en el mapa")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    Double longitud) {
}
