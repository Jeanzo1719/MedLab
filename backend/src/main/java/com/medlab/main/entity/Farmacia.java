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

@Entity
@Table(name = "farmacias")
@Getter
@Setter
@NoArgsConstructor
public class Farmacia {

	@Id
	@Column(name = "usuario_id")
	private Integer usuarioId;

	@Column(name = "nombre_farmacia")
	private String nombreFarmacia;

	@Column(precision = 10, scale = 8)
	private BigDecimal latitud;

	@Column(precision = 11, scale = 8)
	private BigDecimal longitud;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_aprobacion_id")
	private EstadoFarmacia estadoAprobacion;
}
