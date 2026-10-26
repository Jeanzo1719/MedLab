package com.medlab.main.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Datos de la farmacia para el panel. "direccion" viene armada para mostrarla tal cual. */
public record DatosFarmaciaDto(
    Integer id,
    String nombreFarmacia,
    String tipoViaPrincipal,
    String numeroViaPrincipal,
    String letraViaPrincipal,
    String numeroViaGeneradora,
    String letraViaGeneradora,
    String numeroPredio,
    String complemento,
    String direccion,
    String horarioAtencion,
    BigDecimal latitud,
    BigDecimal longitud,
    String estado,
    LocalDateTime fechaUltimaActualizacion) {
}