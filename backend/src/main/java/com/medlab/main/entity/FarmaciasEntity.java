package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FarmaciasEntity - mapea la tabla farmacias.
 * La fecha_ultima_actualizacion solo tiene DEFAULT en la base (sin ON UPDATE): al crear la
 * fila la pone la base, al editar los datos la fija el panel de la farmacia (HU-11) y al
 * aprobarla o rechazarla la fija AprobacionFarmaciasServiceImpl (HU-06).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "farmacias")
public class FarmaciasEntity {

  @Id
  @Column(name = "usuario_id")
  private Integer usuarioId;

  @Column(name = "nombre_farmacia", nullable = false, length = 150)
  private String nombreFarmacia;

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

  @Column(name = "complemento", length = 100)
  private String complemento;

  @Column(name = "horario_atencion", length = 100)
  private String horarioAtencion;

  @Column(name = "nit", nullable = false, length = 20)
  private String nit;

  @Column(name = "rut", nullable = false, length = 20)
  private String rut;

  @Column(name = "matricula_mercantil", nullable = false, length = 20)
  private String matriculaMercantil;

  @Column(name = "identificacion_director_tecnico", nullable = false, length = 10)
  private String identificacionDirectorTecnico;

  @Column(name = "licencia_sanitaria", nullable = false, length = 50)
  private String licenciaSanitaria;

  @Column(name = "latitud", nullable = false, precision = 10, scale = 8)
  private BigDecimal latitud;

  @Column(name = "longitud", nullable = false, precision = 11, scale = 8)
  private BigDecimal longitud;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_aprobacion_id", nullable = false)
  private EstadosFarmaciaEntity estadoAprobacion;

  /** Última vez que la farmacia editó sus datos o cambió de estado; al crear la fila la pone la
   * base (DEFAULT). */
  @Column(name = "fecha_ultima_actualizacion", nullable = false, insertable = false)
  private LocalDateTime fechaUltimaActualizacion;
}
