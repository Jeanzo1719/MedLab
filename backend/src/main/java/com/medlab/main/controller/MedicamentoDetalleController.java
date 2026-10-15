package com.medlab.main.controller;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.exception.MedicamentoNoEncontradoException;
import com.medlab.main.service.MedicamentoDetalleService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Atiende el detalle de un medicamento cuando el usuario lo selecciona de los
 * resultados de la búsqueda. No tiene lógica propia: se la pasa al service.
 */
@Slf4j
@Controller
@Validated
@RequiredArgsConstructor
public class MedicamentoDetalleController {

  private final MedicamentoDetalleService medicamentoDetalleService;

  /**
   * Responde con el detalle del medicamento cuando el frontend se suscribe a
   * /app/medicamentos/detalle.
   *
   * El id llega como un dato extra de la suscripción llamado medicamentoId. No se llama "id"
   * porque STOMP ya usa ese nombre para identificar la suscripción. Si no es un número
   * positivo, no se busca nada y el error queda en el log del backend.
   */
  @SubscribeMapping("/medicamentos/detalle")
  public MedicamentoDetalleDto detalle(
      @Header("medicamentoId") @NotNull @Positive Integer medicamentoId) {
    return medicamentoDetalleService.obtener(medicamentoId);
  }

  /** Si el medicamento no existe, deja solo un aviso corto en el log (sin el error largo). */
  @MessageExceptionHandler(MedicamentoNoEncontradoException.class)
  public void medicamentoNoEncontrado(MedicamentoNoEncontradoException ex) {
    log.warn(ex.getMessage());
  }
}