package com.medlab.main.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
 * Tabla farmacias. Mapea lo que usan la landing y el panel (HU-11). Los datos legales (nit, rut,
 * licencia, director técnico) no se mapean a propósito: ninguna pantalla los toca y así no se
 * pueden modificar por accidente.
 */
@Entity
@Table(name = "farmacias")
@Getter
@Setter
@NoArgsConstructor
public class Farmacia {

  /** Id de la farmacia. */
  @Id
  @Column(name = "usuario_id")
  private Integer usuarioId;

  /** Nombre de la farmacia. */
  @Column(name = "nombre_farmacia", nullable = false, length = 150)
  private String nombreFarmacia;

  /** Coordenadas del marcador en el mapa. */
  @Column(precision = 10, scale = 8)
  private BigDecimal latitud;

  @Column(precision = 11, scale = 8)
  private BigDecimal longitud;

  /** Estado (pendiente, aprobada o suspendida); se carga solo cuando se usa. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "estado_aprobacion_id")
  private EstadoFarmacia estadoAprobacion;

  /** Dirección por partes, como la pide el formato colombiano (Calle 10A # 5B - 20). */
  @Column(name = "tipo_via_principal", nullable = false, length = 20)
  private String tipoViaPrincipal;

  @Column(name = "numero_via_principal", nullable = false, length = 10)
  private String numeroViaPrincipal;

  @Column(name = "letra_via_principal", length = 5)
  private String letraViaPrincipal;

  @Column(name = "numero_via_generadora", nullable = false, length = 10)
  private String numeroViaGeneradora;

  @Column(name = "letra_via_generadora", length = 5)
  private String letraViaGeneradora;

  @Column(name = "numero_predio", nullable = false, length = 10)
  private String numeroPredio;

  @Column(length = 100)
  private String complemento;

  /** Texto libre, por ejemplo "Lunes a sábado 8:00 a 20:00". */
  @Column(name = "horario_atencion", length = 100)
  private String horarioAtencion;

  /** Última vez que la farmacia editó sus datos. */
  @Column(name = "fecha_ultima_actualizacion", nullable = false)
  private LocalDateTime fechaUltimaActualizacion;
}