package com.medlab.main.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * esta es una clase java que representa la tabla estados_farmacia: pendiente, aprobada o suspendida (los que carga V2)
 * solo tienen id y nombre. Representan listas fijas de opciones que otras tablas referencian
 */
@Entity
@Table(name = "estados_farmacia")
@Getter
@Setter
@NoArgsConstructor
public class EstadoFarmacia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre;
}
