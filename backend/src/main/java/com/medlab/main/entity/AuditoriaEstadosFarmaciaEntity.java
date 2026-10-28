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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "auditoria_estados_farmacia")
public class AuditoriaEstadosFarmaciaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "farmacia_id", nullable = false)
  private FarmaciasEntity farmacia;

  @Column(name = "usuario_admin_id")
  private Integer usuarioAdminId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_anterior_id", nullable = false)
  private EstadosFarmaciaEntity estadoAnterior;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estado_nuevo_id", nullable = false)
  private EstadosFarmaciaEntity estadoNuevo;

  @Column(name = "fecha", nullable = false, insertable = false, updatable = false)
  private LocalDateTime fecha;
}
