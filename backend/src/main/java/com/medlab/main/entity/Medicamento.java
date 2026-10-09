package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * este archivo es una clase que extrae cada objeto de la tabla medicamentos
 */
@Entity
@Table(name = "medicamentos")
@Getter
@Setter
@NoArgsConstructor
public class Medicamento {

	/**
	 * el número que identifica a cada medicamento. Lo pone la base de datos automáticamente al guardarlo (1, 2, 3…).
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * el nombre de marca con el que se vende, por ejemplo "Dolex".
	 */
	@Column(name = "nombre_comercial")
	private String nombreComercial;

	/**
	 * la sustancia del medicamento que hace efecto, por ejemplo "Acetaminofén". El buscador busca tanto por este campo como por el nombre comercial.
	 */
	@Column(name = "principio_activo")
	private String principioActivo;

	/**
	 * el grupo al que pertenece, por ejemplo "Analgésicos". Igual que el estado de la farmacia, guarda el número de la categoría y JPA trae el resto cuando se necesita.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	/**
	 * cómo viene empacado, por ejemplo "500 mg x 10".
	 */
	private String presentacion;

	/**
	 * la forma en que viene, por ejemplo tableta o jarabe. Funciona igual que categoria.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forma_farmaceutica_id")
	private FormaFarmaceutica formaFarmaceutica;
}
