package com.medlab.main.controller;

import com.medlab.main.dto.FarmaciaCercanaDto;
import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.dto.FarmaciaPendienteDto;
import com.medlab.main.exception.FarmaciaNotFoundException;
import com.medlab.main.exception.FarmaciaNotPendienteException;
import com.medlab.main.service.AprobacionFarmaciasService;
import com.medlab.main.service.FarmaciasService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Puerta de entrada al backend para el mapa de la landing. Recibe los pedidos del frontend
 * por WebSocket y se los pasa al service. No tiene lógica propia.
 */
@Slf4j
@Controller
@Validated
@AllArgsConstructor
public class FarmaciasController {

  /** Destino donde se publica la lista de pendientes cada vez que una farmacia se aprueba o se
   * rechaza. */
  private static final String DESTINO_FARMACIAS_PENDIENTES = "/topic/farmacias/pendientes";

  private final FarmaciasService farmaciasService;
  private final AprobacionFarmaciasService aprobacionFarmaciasService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Responde con la lista de farmacias aprobadas cuando el frontend se suscribe a
   * /app/farmacias/aprobadas.
   */
  @SubscribeMapping("/farmacias/aprobadas")
  public List<FarmaciaMapaDto> getFarmaciasAprobadas() {
    return farmaciasService.getAllFarmaciasAprobadas();
  }

  /**
   * Responde con las farmacias aprobadas más cercanas que tienen el medicamento disponible,
   * cuando el frontend se suscribe a /app/farmacias/cercanas.
   *
   * Los datos llegan como datos extra de la suscripción: medicamentoId (el medicamento que
   * se busca), latitud y longitud (dónde está el usuario). Las anotaciones revisan que
   * vengan y que sean válidos: el id mayor que 0, la latitud entre -90 y 90 y la longitud
   * entre -180 y 180. Si algo no cumple, no se busca nada y el error queda en el log del
   * backend. Si todo está bien, se lo pasa al service.
   */
  @SubscribeMapping("/farmacias/cercanas")
  public List<FarmaciaCercanaDto> getFarmaciasCercanas(
      @Header("medicamentoId") @NotNull @Positive Long medicamentoId,
      @Header("latitud") @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitud,
      @Header("longitud") @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitud) {
    return farmaciasService.getFarmaciasCercanasConDisponibilidad(medicamentoId, latitud, longitud);
  }

  /**
   * Responde con las farmacias que esperan aprobación, con sus datos de validación, cuando el
   * frontend se suscribe a /app/farmacias/pendientes. Para recibir los cambios después, el
   * frontend también se suscribe a /topic/farmacias/pendientes.
   */
  @SubscribeMapping("/farmacias/pendientes")
  public List<FarmaciaPendienteDto> getFarmaciasPendientes() {
    return farmaciasService.getAllFarmaciasPendientes();
  }

  /**
   * Aprueba una farmacia pendiente cuando el frontend envía un mensaje a
   * /app/farmacias/aprobar con el header farmaciaId (un número mayor que 0). Después publica
   * la lista de pendientes actualizada en /topic/farmacias/pendientes. El administrador va en
   * null hasta que exista el inicio de sesión.
   */
  @MessageMapping("/farmacias/aprobar")
  public void approveFarmacia(@Header("farmaciaId") @NotNull @Positive Integer farmaciaId) {
    aprobacionFarmaciasService.approveFarmacia(farmaciaId, null);
    publishFarmaciasPendientes();
  }

  /**
   * Rechaza una farmacia pendiente cuando el frontend envía un mensaje a
   * /app/farmacias/rechazar con el header farmaciaId. Funciona igual que approveFarmacia.
   */
  @MessageMapping("/farmacias/rechazar")
  public void rejectFarmacia(@Header("farmaciaId") @NotNull @Positive Integer farmaciaId) {
    aprobacionFarmaciasService.rejectFarmacia(farmaciaId, null);
    publishFarmaciasPendientes();
  }

  /** Si la farmacia no existe o ya no está pendiente, deja solo un aviso corto en el log del
   * backend. El cliente no recibe respuesta y la lista de pendientes no se vuelve a publicar. */
  @MessageExceptionHandler({FarmaciaNotFoundException.class,
      FarmaciaNotPendienteException.class})
  public void handleCambioEstadoFarmaciaException(RuntimeException ex) {
    log.warn(ex.getMessage());
  }

  /** Envía la lista actual de farmacias pendientes a quienes escuchan
   * /topic/farmacias/pendientes. */
  private void publishFarmaciasPendientes() {
    messagingTemplate.convertAndSend(DESTINO_FARMACIAS_PENDIENTES,
        farmaciasService.getAllFarmaciasPendientes());
  }
}
