package com.medlab.main.service;

/**
 * Qué hace: avisa por WebSocket a todos los clientes que están viendo un medicamento que su
 * detalle cambió, enviándoles el detalle actualizado.
 *
 * Cómo entenderlo: es como el altavoz de una tienda: cuando cambia el estado de un producto,
 * el empleado lo anuncia y todos los que están mirando ese producto reciben el nuevo cartel
 * sin tener que volver a preguntar.
 */
public interface MedicamentoDetalleNotificador {

  /** Inicio del destino al que se suscriben los clientes; se le agrega el id del medicamento. */
  String DESTINO_BASE = "/topic/medicamentos/";

  /**
   * Envía el detalle actualizado de un medicamento a quienes lo están viendo.
   *
   * medicamentoId: id del medicamento cuyo detalle cambió.
   */
  void publishMedicamentoDetalleUpdate(Long medicamentoId);
}
