package com.medlab.main.dto;

import java.time.LocalDateTime;

/** Un cambio de estado de una farmacia. automatico es true cuando lo hizo el sistema. */
public record AuditoriaEstadoDto(Integer id, String estadoAnterior, String estadoNuevo,
    Integer usuarioAdminId, boolean automatico, LocalDateTime fecha) {
}