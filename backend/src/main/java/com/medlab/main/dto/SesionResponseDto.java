package com.medlab.main.dto;

import java.time.LocalDateTime;

/**
 * Lo que recibe el frontend al iniciar sesión: el token que debe mandar en cada llamada, el
 * rol de la cuenta y la fecha en que la sesión deja de valer.
 */
public record SesionResponseDto(String token, String rol, String identificacion,
    LocalDateTime expiraEn) {
}
