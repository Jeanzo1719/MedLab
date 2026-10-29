package com.medlab.main.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que la farmacia puede editar. Latitud y longitud son opcionales y van juntas: si no se
 * envían, el marcador del mapa se queda donde estaba.
 */
public record GuardarDatosFarmaciaDto(
    @NotBlank(message = "El nombre de la farmacia es obligatorio")
    @Size(max = 150, message = "El nombre no puede pasar de 150 caracteres")
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

    @Size(max = 100, message = "El horario no puede pasar de 100 caracteres")
    String horarioAtencion,

    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    BigDecimal latitud,

    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    BigDecimal longitud) {
}