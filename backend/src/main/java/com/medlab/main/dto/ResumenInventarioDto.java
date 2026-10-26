package com.medlab.main.dto;

import java.util.Map;

/**
 * Conteo de productos por estado para el dashboard. porEstado siempre trae los tres estados,
 * aunque alguno esté en 0, para que el frontend no tenga que rellenar huecos.
 */
public record ResumenInventarioDto(Map<String, Long> porEstado, long total) {
}