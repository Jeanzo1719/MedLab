package com.medlab.main.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Qué hace: representa una fila de la tabla tipos_calificacion, la lista de opciones que
 * un usuario puede elegir al calificar un reporte.
 *
 * Cómo entenderlo: es como las opciones de una encuesta: el usuario no escribe lo que
 * quiera, escoge una de las opciones ya definidas.
 *
 * Detalles técnicos: cada Calificacion apunta a un TipoCalificacion por su id. El nombre es
 * único en la tabla. MensajeDisponibilidadService cuenta las calificaciones recientes según
 * este nombre: "Si hay", "Bajo pedido" o "No hay", escritos igual que en la base de datos.
 * En HU-4 el backend solo lee esta tabla: no inserta ni modifica tipos.
 */
@Entity
@Table(name = "tipos_calificacion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipoCalificacion {

  /** Número que identifica al tipo. Lo asigna la base de datos (1, 2, 3...). */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /** Nombre del tipo, por ejemplo "Si hay". Es único. */
  @Column(name = "nombre", nullable = false, length = 30)
  private String nombre;
}