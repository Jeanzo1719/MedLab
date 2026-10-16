package com.medlab.main.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.medlab.main.dto.MedicamentoDetalleDto;

import lombok.RequiredArgsConstructor;

/**
 * Qué hace: avisa por WebSocket a todos los clientes que están viendo un medicamento que su
 * detalle cambió, enviándoles el detalle actualizado.
 *
 * Cómo entenderlo: es como el altavoz de una tienda: cuando cambia el estado de un producto,
 * el empleado lo anuncia y todos los que están mirando ese producto reciben el nuevo cartel
 * sin tener que volver a preguntar.
 *
 * Detalles técnicos: publicar(medicamentoId) pide el detalle actualizado a
 * MedicamentoDetalleService y lo envía a /topic/medicamentos/{medicamentoId} con
 * SimpMessagingTemplate. El frontend recibe el estado inicial suscribiéndose a
 * /app/medicamentos/detalle (con el header medicamentoId) y los cambios suscribiéndose a
 * /topic/medicamentos/{medicamentoId}; ambos entregan el mismo MedicamentoDetalleDto. Lo debe
 * llamar el código que modifica una disponibilidad (cambia fecha_hora_reporte), después de
 * guardar el cambio. Si el medicamento no existe, MedicamentoDetalleService lanza
 * MedicamentoNoEncontradoException y no se envía nada. No modifica la base de datos.
 */
@Service
@RequiredArgsConstructor
public class MedicamentoDetalleNotificador {

  /** Inicio del destino al que se suscriben los clientes; se le agrega el id del medicamento. */
  public static final String DESTINO_BASE = "/topic/medicamentos/";

  private final MedicamentoDetalleService medicamentoDetalleService;
  private final SimpMessagingTemplate mensajeria;

  /**
   * Envía el detalle actualizado de un medicamento a quienes lo están viendo.
   *
   * medicamentoId: id del medicamento cuyo detalle cambió.
   */
  public void publicar(Integer medicamentoId) {
    MedicamentoDetalleDto detalle = medicamentoDetalleService.obtener(medicamentoId);
    mensajeria.convertAndSend(DESTINO_BASE + medicamentoId, detalle);
  }
}