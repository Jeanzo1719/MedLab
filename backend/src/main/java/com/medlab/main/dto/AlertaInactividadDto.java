package com.medlab.main.dto;

import java.time.LocalDateTime;

/** Aviso que recibe una farmacia cuando se genera una alerta de inactividad. */
public record AlertaInactividadDto(Integer farmaciaId, long alertasPendientes,
    int alertasParaSuspender, boolean suspendida, LocalDateTime fecha) {
}