package com.medlab.main.dto;

/** Resultado de una revisión de inactividad: cuántas alertas se crearon y cuántas farmacias se suspendieron. */
public record RevisionInactividadDto(int alertasGeneradas, int farmaciasSuspendidas) {
}