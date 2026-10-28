package com.medlab.main.controller;

import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.exception.MedicamentoNotFoundException;
import com.medlab.main.service.MedicamentoDetalleService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Qué hace: atiende el pedido del detalle de un medicamento cuando el usuario lo selecciona
 * de los resultados de la búsqueda.
 *
 * Cómo entenderlo: es la ventanilla de atención: recibe el pedido, se lo pasa al empleado
 * encargado (el service) y entrega la respuesta. No tiene lógica propia.
 *
 * Detalles técnicos: el frontend se suscribe a /app/medicamentos/detalle por WebSocket y
 * envía el id del medicamento en un dato extra de la suscripción llamado medicamentoId. Se
 * revisa que sea un número positivo y se pasa a MedicamentoDetalleService. La respuesta, un
 * MedicamentoDetalleDto, se convierte a JSON y se envía una sola vez a quien se suscribió.
 * Si el dato no es válido, no se busca nada y el error queda en el log del backend.
 */
@Slf4j
@Controller
@Validated
@AllArgsConstructor
public class MedicamentoDetalleController {

  private final MedicamentoDetalleService medicamentoDetalleService;

  /**
   * Responde con el detalle del medicamento cuando el frontend se suscribe a
   * /app/medicamentos/detalle.
   *
   * medicamentoId: id del medicamento. No se llama "id" porque STOMP ya usa ese nombre para
   * identificar la suscripción.
   */
  @SubscribeMapping("/medicamentos/detalle")
  public MedicamentoDetalleDto getMedicamentoDetalle(
      @Header("medicamentoId") @NotNull @Positive Long medicamentoId) {
    return medicamentoDetalleService.getMedicamentoDetalleById(medicamentoId);
  }

  /** Si el medicamento no existe, deja solo un aviso corto en el log del backend. El cliente
   * no recibe respuesta. */
  @MessageExceptionHandler(MedicamentoNotFoundException.class)
  public void handleMedicamentoNotFound(MedicamentoNotFoundException ex) {
    log.warn(ex.getMessage());
  }
}
