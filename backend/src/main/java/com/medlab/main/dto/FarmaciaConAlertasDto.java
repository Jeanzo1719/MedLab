package com.medlab.main.dto;

import java.time.LocalDateTime;

/** Farmacia con alertas de inactividad registradas. */
public record FarmaciaConAlertasDto(Integer id, String nombre, String estado, long totalAlertas,
    long alertasPendientes, LocalDateTime ultimaAlerta) {
}