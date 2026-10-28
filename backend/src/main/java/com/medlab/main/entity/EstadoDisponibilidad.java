package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Los tres estados posibles de un producto en una farmacia. Los nombres vienen
 * sembrados en V1; las constantes evitan escribirlos a mano en cada service.
 */
@Entity
@Table(name = "estados_disponibilidad")
@Getter
@NoArgsConstructor
public class EstadoDisponibilidad {

  public static final String DISPONIBLE = "disponible";
  public static final String AGOTADO = "agotado";
  public static final String BAJO_PEDIDO = "bajo_pedido";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 30)
  private String nombre;
}