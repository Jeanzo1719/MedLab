package com.medlab.main.dto;

import java.time.LocalDateTime;

/** Publicación de una farmacia vista por el administrador. */
public record DisponibilidadAdminDto(Integer id, String medicamento, String presentacion,
    String estado, int cantidad, long totalCalificaciones, LocalDateTime fechaHoraReporte) {
}