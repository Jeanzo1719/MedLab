package com.medlab.main.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Qué hace: lleva lo que el paciente/cuidador envió al calificar el reporte de una farmacia.
 *
 * Cómo entenderlo: es la boleta de la encuesta: dice cuál reporte se califica y cuál de las
 * tres opciones eligió el usuario.
 *
 * Detalles técnicos: lo recibe CalificacionController desde el cuerpo JSON de
 * POST /api/calificaciones. El id del usuario no viaja en el cuerpo: sale del token de la
 * sesión, para que nadie pueda calificar en nombre de otra persona.
 *
 * disponibilidadId: id del reporte de disponibilidad que se califica.
 * tipoCalificacionId: id del tipo elegido (1 "Si hay", 2 "Bajo pedido", 3 "No hay").
 */
public record CalificacionRequestDto(@NotNull @Positive Integer disponibilidadId,
    @NotNull @Positive Integer tipoCalificacionId) {
}
