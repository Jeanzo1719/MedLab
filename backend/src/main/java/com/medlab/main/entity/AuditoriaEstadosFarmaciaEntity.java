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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Qué hace: representa una fila de la tabla auditoria_estados_farmacia, es decir, el registro
 * de un cambio de estado de una farmacia.
 *
 * Cómo entenderlo: es como la bitácora de un trámite: cada vez que alguien aprueba o rechaza
 * una solicitud, se anota quién lo hizo, cómo estaba antes, cómo quedó y cuándo.
 *
 * Detalles técnicos: cada fila apunta a una FarmaciasEntity y a dos EstadosFarmaciaEntity (el
 * anterior y el nuevo), así se guardan los ids de estados_farmacia y nunca el nombre como
 * texto. El administrador se guarda solo con su id (usuarioAdminId) porque todavía no existe
 * una entity de usuarios; puede ser null mientras no exista el inicio de sesión. La base de
 * datos asigna el id y la fecha al guardar, por eso fecha es de solo lectura desde Java.
 * AprobacionFarmaciasServiceImpl crea una fila cada vez que aprueba o rechaza una farmacia.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "auditoria_estados_farmacia")
public class AuditoriaEstadosFarmaciaEntity {

  /** Número que identifica al registro. Lo asigna la base de datos. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  /** Farmacia a la que se le cambió el estado. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmacia_id", nullable = false)
  private FarmaciasEntity farmacia;

  /** Id del administrador que hizo el cambio. Es null mientras no exista el inicio de sesión. */
  @Column(name = "usuario_admin_id")
  private Integer usuarioAdminId;

  /** Estado que tenía la farmacia antes del cambio, por ejemplo "pendiente". */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_anterior_id", nullable = false)
  private EstadosFarmaciaEntity estadoAnterior;

  /** Estado con el que quedó la farmacia, "aprobada" o "rechazada". */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_nuevo_id", nullable = false)
  private EstadosFarmaciaEntity estadoNuevo;

  /** Fecha y hora del cambio. La pone la base de datos al guardar. */
  @Column(name = "fecha", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fecha;
}
