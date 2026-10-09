package com.medlab.main.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * este archivo representa la tabla farmacias
 *
 * por ahora solo mapea las columnas que estan funcionando en la landing
 * la tabla tiene mas columnas pero JPA permite ignorarlas
 */
@Entity
@Table(name = "farmacias")
@Getter
@Setter
@NoArgsConstructor
public class Farmacia {

	/**
	 * este atributo consulta la identificacion de cada farmacia (id)
	 */
	@Id
	@Column(name = "usuario_id")
	private Integer usuarioId;

	/**
	 * este atributo consulta el nombre de la farmacia
	 */
	@Column(name = "nombre_farmacia")
	private String nombreFarmacia;

	/**
	 * estos atributos consultan las coordenadas de la farmacia: latitud es norte-sur y longitud es este-oeste
	 * on las dos juntas se ubica el marcador en el mapa. Guardan los números con todos sus decimales exactos,
	 * para que la ubicacion no se corra
	 */
	@Column(precision = 10, scale = 8)
	private BigDecimal latitud;

	@Column(precision = 11, scale = 8)
	private BigDecimal longitud;

	/**
	 * este atributo consulta el estado de una farmacia ( pendiente, aprobada o suspendida)
	 *
	 * la farmacia no guarda el texto del estado, sino el número del estado (columna estado_aprobacion_id).
	 * Con ese número, JPA trae el estado completo de la tabla estados_farmacia. Solo lo trae cuando alguien
	 * lo necesita, no siempre (eso es el LAZY)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_aprobacion_id")
	private EstadoFarmacia estadoAprobacion;
}
