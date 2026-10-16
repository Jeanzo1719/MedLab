package com.medlab.main.dto;

/**
 * Qué hace: lista los tres niveles posibles de confianza de un reporte: ALTA, MEDIA y BAJA.
 *
 * Cómo entenderlo: es como un semáforo en cada reporte: indica de un vistazo qué tanto se
 * puede confiar en la información que muestra.
 *
 * Detalles técnicos: ConfianzaService elige el nivel según el puntaje que calcula (los
 * umbrales están definidos allí), ConfianzaDto lo transporta y MedicamentoDetalleService
 * lo usa para armar el mensaje que se muestra ("Confianza alta", "Confianza media" o
 * "Confianza baja"). Viaja al frontend como texto: el nombre del valor.
 */
public enum NivelConfianza {
  ALTA, MEDIA, BAJA
}