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
 * esta es una clase java que representa la tabla formas_farmaceuticas: cómo viene el medicamento, por ejemplo tableta o jarabe.
 * solo tienen id y nombre. Representan listas fijas de opciones que otras tablas referencian
 */
@Entity
@Table(name = "formas_farmaceuticas")
@Getter
@Setter
@NoArgsConstructor
public class FormaFarmaceutica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre;
}
