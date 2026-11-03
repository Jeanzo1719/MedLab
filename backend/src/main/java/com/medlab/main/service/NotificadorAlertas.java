package com.medlab.main.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.medlab.main.dto.AlertaInactividadDto;
import com.medlab.main.dto.DisponibilidadSospechosaDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Avisos en vivo de HU-9 por WebSocket. Un aviso fallido solo se registra: lo importante ya
 * quedó guardado en la BD y el panel puede consultarlo cuando quiera.
 *
 * Los topics están protegidos por SesionStompInterceptor: los avisos de calificaciones son solo
 * para administradores y las alertas de una farmacia, solo para esa farmacia.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificadorAlertas {

  /** Se le agrega el id de la farmacia y "/alertas". */
  public static final String DESTINO_FARMACIA_BASE = "/topic/farmacias/";
  public static final String DESTINO_ADMIN_CALIFICACIONES = "/topic/admin/calificaciones";

  private final SimpMessagingTemplate mensajeria;

  public void alertaInactividad(AlertaInactividadDto alerta) {
    enviar(DESTINO_FARMACIA_BASE + alerta.farmaciaId() + "/alertas", alerta);
  }

  public void calificacionesSospechosas(DisponibilidadSospechosaDto disponibilidad) {
    enviar(DESTINO_ADMIN_CALIFICACIONES, disponibilidad);
  }

  private void enviar(String destino, Object mensaje) {
    try {
      mensajeria.convertAndSend(destino, mensaje);
    } catch (RuntimeException e) {
      log.warn("No se pudo enviar el aviso a {}", destino, e);
    }
  }
}