package com.medlab.main.dto;

/**
 * Qué hace: indica qué dicen las calificaciones más recientes de un reporte sobre la
 * disponibilidad del medicamento y trae el mensaje listo para mostrar.
 *
 * Cómo entenderlo: es como el letrero de una tienda que resume lo que dicen los últimos
 * clientes: "sí hay", "hay bajo pedido", "no hay" o "no se ponen de acuerdo".
 *
 * Detalles técnicos: MensajeDisponibilidadService decide cuál valor corresponde a cada reporte
 * y MedicamentoDetalleService lo incluye en DisponibilidadReporteDto. Viaja al frontend como
 * JSON con el nombre del valor (por ejemplo "DISPONIBLE"), para que el frontend pueda elegir
 * colores o íconos, junto con el mensaje de texto.
 *
 * DISPONIBLE: la mayoría de las calificaciones recientes dice "Si hay".
 * BAJO_PEDIDO: la mayoría dice "Bajo pedido".
 * NO_DISPONIBLE: la mayoría dice "No hay".
 * SIN_CONSENSO: dos o más tipos empatan en el primer lugar.
 * SIN_CALIFICACIONES: el reporte no tiene calificaciones.
 */
public enum TendenciaDisponibilidad {

  DISPONIBLE("Según los últimos reportes, el medicamento está disponible"),
  BAJO_PEDIDO("Según los últimos reportes, el medicamento está disponible bajo pedido"),
  NO_DISPONIBLE("Según los últimos reportes, el medicamento no está disponible"),
  SIN_CONSENSO("Los últimos reportes no coinciden"),
  SIN_CALIFICACIONES("sin calificaciones aún");

  private final String mensaje;

  TendenciaDisponibilidad(String mensaje) {
    this.mensaje = mensaje;
  }

  /** Texto listo para mostrar al usuario. */
  public String getMensaje() {
    return mensaje;
  }
}