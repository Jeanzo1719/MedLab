package com.medlab.main.dto;

import java.time.LocalDateTime;

/**
 * La sesión que está usando el token actual, para que el frontend confirme que sigue viva
 * después de recargar la página.
 */
public record SesionActualDto(Integer id, String identificacion, String rol,
    LocalDateTime expiraEn) {
}
