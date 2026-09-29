package com.medlab.main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Pharmacy as it will be stored in the "farmacias" MongoDB collection. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Farmacia {

	private String id;
	private String usuarioId;
	private String nombreFarmacia;
	private EstadoAprobacion estadoAprobacion;
	private double latitud;
	private double longitud;
}
