package com.medlab.main.dto;

import java.time.LocalDateTime;

/** Calificación de una publicación, con quién la hizo, para decidir si se elimina. */
public record CalificacionAdminDto(Long id, Integer usuarioId, String identificacionUsuario,
    String tipo, LocalDateTime fechaHora) {
}