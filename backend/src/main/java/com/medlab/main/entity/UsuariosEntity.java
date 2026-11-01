package com.medlab.main.entity;

import java.time.LocalDateTime;

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
 * UsuariosEntity - mapea la tabla usuarios, la cuenta que comparten pacientes y farmacias.
 *
 * La identificacion es la cedula del paciente/cuidador (6 a 10 digitos) o el UUID que se
 * genera para una farmacia, porque una farmacia no tiene cedula. La contrasena nunca se
 * guarda en texto plano: contrasena_hash guarda el resultado de Argon2.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuariosEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "identificacion", nullable = false, length = 36)
  private String identificacion;

  @Column(name = "correo", nullable = false, length = 150)
  private String correo;

  @Column(name = "contrasena_hash", nullable = false, length = 255)
  private String contrasenaHash;

  @Column(name = "telefono", nullable = false, length = 20)
  private String telefono;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "rol_id", nullable = false)
  private RolesEntity rol;

  /** La pone la base al crear la fila (DEFAULT CURRENT_TIMESTAMP). */
  @Column(name = "fecha_registro", nullable = false, insertable = false)
  private LocalDateTime fechaRegistro;
}
