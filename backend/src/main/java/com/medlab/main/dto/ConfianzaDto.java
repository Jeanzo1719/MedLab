package com.medlab.main.dto;

/**
 * Qué hace: lleva el nivel de confianza de un reporte y el puntaje con el que se calculó.
 *
 * Cómo entenderlo: es como la nota de una evaluación: el puntaje es el número (0.82) y el
 * nivel es la etiqueta que le corresponde ("ALTA").
 *
 * Detalles técnicos: lo crea ConfianzaService a partir de las calificaciones y la antigüedad
 * del reporte, MedicamentoDetalleService lo incluye en cada DisponibilidadReporteDto y viaja
 * al frontend como JSON.
 *
 * nivel: ALTA, MEDIA o BAJA, según los umbrales definidos en ConfianzaService.
 * puntaje: número de 0 a 1, redondeado a dos decimales.
 */
public record ConfianzaDto(NivelConfianza nivel, double puntaje) {
}