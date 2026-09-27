package com.medlab.main.dto;

/**
 * Public view of a pharmacy for the visitor map. Exposes only what the map
 * needs; the approval state and owner are never sent to the client.
 */
public record FarmaciaMapaDto(String id, String nombre, double latitud, double longitud) {}
