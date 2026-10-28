package com.medlab.main.service.impl;

import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.service.MedicamentoDetalleNotificador;
import com.medlab.main.service.MedicamentoDetalleService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Qué hace: avisa por WebSocket a todos los clientes que están viendo un medicamento que su
 * detalle cambió, enviándoles el detalle actualizado.
 *
 * Cómo entenderlo: es como el altavoz de una tienda: cuando cambia el estado de un producto,
 * el empleado lo anuncia y todos los que están mirando ese producto reciben el nuevo cartel
 * sin tener que volver a preguntar.
 *
 * Detalles técnicos: publishMedicamentoDetalleUpdate pide el detalle actualizado a
 * MedicamentoDetalleService y lo envía a /topic/medicamentos/{medicamentoId} con
 * SimpMessagingTemplate. El frontend recibe el estado inicial suscribiéndose a
 * /app/medicamentos/detalle (con el header medicamentoId) y los cambios suscribiéndose a
 * /topic/medicamentos/{medicamentoId}; ambos entregan el mismo MedicamentoDetalleDto. Lo
 * debe llamar el código que modifica una disponibilidad (cambia fecha_hora_reporte), después
 * de guardar el cambio. Si el medicamento no existe, MedicamentoDetalleService lanza
 * MedicamentoNotFoundException y no se envía nada. No modifica la base de datos.
 */
@Service
@AllArgsConstructor
public class MedicamentoDetalleNotificadorImpl implements MedicamentoDetalleNotificador {

  private final MedicamentoDetalleService medicamentoDetalleService;
  private final SimpMessagingTemplate mensajeria;

  @Override
  public void publishMedicamentoDetalleUpdate(Long medicamentoId) {
    MedicamentoDetalleDto detalle = medicamentoDetalleService
        .getMedicamentoDetalleById(medicamentoId);
    mensajeria.convertAndSend(DESTINO_BASE + medicamentoId, detalle);
  }
}
