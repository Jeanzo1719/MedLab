package com.medlab.main.dto;

/** Publicación con muchas calificaciones "No hay" recientes, para que el admin la revise. */
public record DisponibilidadSospechosaDto(Integer disponibilidadId, Integer farmaciaId,
    String farmacia, String medicamento, String presentacion, long calificacionesNoHay,
    long calificacionesRevisadas) {
}