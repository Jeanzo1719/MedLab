package com.medlab.main.dto;

import java.time.LocalDateTime;

/** Fila del historial de farmacias. */
public record FarmaciaHistorialDto(Integer id, String nombre, String estado,
    LocalDateTime fechaUltimaActualizacion, long alertasPendientes) {
}