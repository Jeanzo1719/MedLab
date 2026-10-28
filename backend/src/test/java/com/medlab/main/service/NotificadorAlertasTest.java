package com.medlab.main.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.medlab.main.dto.AlertaInactividadDto;
import com.medlab.main.dto.DisponibilidadSospechosaDto;

@ExtendWith(MockitoExtension.class)
class NotificadorAlertasTest {

  @Mock
  private SimpMessagingTemplate mensajeria;

  @InjectMocks
  private NotificadorAlertas notificador;

  private AlertaInactividadDto alerta() {
    return new AlertaInactividadDto(9002, 1L, 3, false, LocalDateTime.of(2026, 10, 28, 14, 15, 4));
  }

  @Test
  void laAlertaDeInactividadVaAlTopicDeEsaFarmacia() {
    AlertaInactividadDto alerta = alerta();

    notificador.alertaInactividad(alerta);

    verify(mensajeria).convertAndSend("/topic/farmacias/9002/alertas", alerta);
  }

  @Test
  void laDisponibilidadSospechosaVaAlTopicDelAdmin() {
    DisponibilidadSospechosaDto dto = mock(DisponibilidadSospechosaDto.class);

    notificador.calificacionesSospechosas(dto);

    verify(mensajeria).convertAndSend("/topic/admin/calificaciones", dto);
  }

  @Test
  void unAvisoFallidoNoRompeAQuienLoLlama() {
    // El dato ya quedó en la BD: que el WebSocket falle no debe deshacer la revisión.
    doThrow(new IllegalStateException("broker caído")).when(mensajeria)
        .convertAndSend(anyString(), any(Object.class));

    assertThatCode(() -> notificador.alertaInactividad(alerta())).doesNotThrowAnyException();
  }
}