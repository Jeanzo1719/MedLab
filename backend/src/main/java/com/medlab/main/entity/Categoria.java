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
 * esta es una clase java que representa la tabla categorias: el grupo terapéutico del medicamento, por ejemplo "Analgésicos"
 * solo tienen id y nombre. Representan listas fijas de opciones que otras tablas referencian
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre;
}
