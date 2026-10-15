package com.medlab.main.dto;

/** Resultado de la confianza: el nivel y el puntaje (de 0 a 1) con el que se calculó. */
public record ConfianzaDto(NivelConfianza nivel, double puntaje) {
}