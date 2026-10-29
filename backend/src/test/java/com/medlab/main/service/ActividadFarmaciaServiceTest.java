package com.medlab.main.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medlab.main.repository.InactividadRepository;

@ExtendWith(MockitoExtension.class)
class ActividadFarmaciaServiceTest {

  @Mock
  private InactividadRepository inactividadRepository;

  @InjectMocks
  private ActividadFarmaciaService servicio;

  @Test
  void registrarActividadMueveLaFechaYAtiendeLasAlertas() {
    LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

    servicio.registrarActividad(9002);

    LocalDateTime despues = LocalDateTime.now();
    ArgumentCaptor<LocalDateTime> fecha = ArgumentCaptor.forClass(LocalDateTime.class);
    // Primero la fecha y luego las alertas: si se invierte, una alerta atendida podría
    // reaparecer en una revisión que corra en medio.
    InOrder orden = inOrder(inactividadRepository);
    orden.verify(inactividadRepository).moverFechaActualizacion(any(Integer.class), fecha.capture());
    orden.verify(inactividadRepository).marcarAlertasAtendidas(9002);

    assertThat(fecha.getValue()).isBetween(antes, despues);
    // Sin fracciones de segundo, igual que el resto de fechas de la BD.
    assertThat(fecha.getValue().getNano()).isZero();
  }

  @Test
  void atenderAlertasNoTocaLaFecha() {
    servicio.atenderAlertas(9002);

    verify(inactividadRepository).marcarAlertasAtendidas(9002);
    verify(inactividadRepository, never()).moverFechaActualizacion(any(), any());
    verifyNoMoreInteractions(inactividadRepository);
  }
}