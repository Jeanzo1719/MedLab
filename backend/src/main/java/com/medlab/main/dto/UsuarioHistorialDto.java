package com.medlab.main.dto;

import java.time.LocalDateTime;

/** Fila del historial de usuarios. nombre es null para el administrador (no tiene perfil). */
public record UsuarioHistorialDto(Integer id, String identificacion, String rol, String nombre,
    LocalDateTime fechaRegistro) {
}