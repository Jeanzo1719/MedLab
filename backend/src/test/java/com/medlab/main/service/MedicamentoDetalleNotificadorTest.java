package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.exception.MedicamentoNotFoundException;
import com.medlab.main.service.impl.MedicamentoDetalleNotificadorImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Pruebas unitarias de MedicamentoDetalleNotificadorImpl: comprueban que el detalle se
 * publica en el destino del medicamento y que no se envía nada si el medicamento no existe.
 * No abren conexiones WebSocket reales.
 */
@ExtendWith(MockitoExtension.class)
class MedicamentoDetalleNotificadorTest {

  @Mock
  private MedicamentoDetalleService medicamentoDetalleService;
  @Mock
  private SimpMessagingTemplate mensajeria;
  @InjectMocks
  private MedicamentoDetalleNotificadorImpl notificador;

  @Test
  void publicaElDetalleEnElDestinoDelMedicamento() {
    MedicamentoDetalleDto detalle = new MedicamentoDetalleDto(7L, "Losartán",
        "Losartán potásico", "Antihipertensivo", "50 mg", "Tableta", List.of());
    when(medicamentoDetalleService.getMedicamentoDetalleById(7L)).thenReturn(detalle);

    notificador.publishMedicamentoDetalleUpdate(7L);

    verify(mensajeria).convertAndSend("/topic/medicamentos/7", detalle);
  }

  @Test
  void siElMedicamentoNoExisteNoEnviaNada() {
    when(medicamentoDetalleService.getMedicamentoDetalleById(99L))
        .thenThrow(new MedicamentoNotFoundException(99L));

    assertThrows(MedicamentoNotFoundException.class,
        () -> notificador.publishMedicamentoDetalleUpdate(99L));

    verify(mensajeria, never()).convertAndSend(anyString(), any(Object.class));
  }
}
