package com.medlab.main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento {

	private String id;
	private String nombreComercial;
	private String principioActivo;
	private String categoria;
	private String presentacion;
	private String formaFarmaceutica;
}
