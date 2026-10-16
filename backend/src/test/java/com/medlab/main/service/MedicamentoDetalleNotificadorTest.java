package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.exception.MedicamentoNoEncontradoException;

/**
 * Pruebas unitarias de MedicamentoDetalleNotificador: comprueban que el detalle se publica en
 * el destino del medicamento y que no se envía nada si el medicamento no existe. No abren
 * conexiones WebSocket reales.
 */
@ExtendWith(MockitoExtension.class)
class MedicamentoDetalleNotificadorTest {

  @Mock
  private MedicamentoDetalleService medicamentoDetalleService;
  @Mock
  private SimpMessagingTemplate mensajeria;
  @InjectMocks
  private MedicamentoDetalleNotificador notificador;

  @Test
  void publicaElDetalleEnElDestinoDelMedicamento() {
    MedicamentoDetalleDto detalle = new MedicamentoDetalleDto(7, "Losartán",
        "Losartán potásico", "Antihipertensivo", "50 mg", "Tableta", List.of());
    when(medicamentoDetalleService.obtener(7)).thenReturn(detalle);

    notificador.publicar(7);

    verify(mensajeria).convertAndSend("/topic/medicamentos/7", detalle);
  }

  @Test
  void siElMedicamentoNoExisteNoEnviaNada() {
    when(medicamentoDetalleService.obtener(99))
        .thenThrow(new MedicamentoNoEncontradoException(99));

    assertThrows(MedicamentoNoEncontradoException.class, () -> notificador.publicar(99));

    verify(mensajeria, never()).convertAndSend(anyString(), any(Object.class));
  }
}