package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PacientesEntity - mapea la tabla pacientes, los datos personales del paciente/cuidador.
 *
 * El id es el mismo de su usuario: la tabla comparte la llave con usuarios para no repetir
 * la cuenta. El segundo_nombre es opcional y guarda los nombres adicionales cuando la
 * persona tiene tres o más nombres.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pacientes")
public class PacientesEntity {

  @Id
  @Column(name = "usuario_id")
  private Integer usuarioId;

  @Column(name = "primer_nombre", nullable = false, length = 50)
  private String primerNombre;

  @Column(name = "segundo_nombre", length = 100)
  private String segundoNombre;

  @Column(name = "apellidos", nullable = false, length = 100)
  private String apellidos;

  @Column(name = "eps_afiliada", nullable = false, length = 100)
  private String epsAfiliada;
}
