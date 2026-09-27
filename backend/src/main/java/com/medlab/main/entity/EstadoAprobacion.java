package com.medlab.main.entity;

/**
 * Approval state of a pharmacy. Embedded in the pharmacy document instead of
 * living in a separate collection, so filtering by state needs no join.
 */
public enum EstadoAprobacion {
  PENDIENTE,
  APROBADA,
  SUSPENDIDA
}
