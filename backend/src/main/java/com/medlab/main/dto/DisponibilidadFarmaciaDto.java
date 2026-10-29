package com.medlab.main.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Un producto del inventario tal como lo ve la farmacia en su panel.
 * stockBajo lo calcula el service con su umbral; el esquema no guarda ese umbral.
 */
public record DisponibilidadFarmaciaDto(
    Integer medicamentoId,
    String nombreComercial,
    String principioActivo,
    String presentacion,
    Integer cantidadDisponible,
    BigDecimal precio,
    String estado,
    boolean stockBajo,
    LocalDateTime fechaHoraReporte) {
}