package com.medlab.main.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.medlab.main.dto.AlertaInactividadDto;
import com.medlab.main.dto.RevisionInactividadDto;
import com.medlab.main.repository.InactividadRepository;

@ExtendWith(MockitoExtension.class)
class InactividadFarmaciaServiceTest {

  @Mock
  private InactividadRepository inactividadRepository;

  @Mock
  private TransactionTemplate transaccion;

  @Mock
  private NotificadorAlertas notificador;

  @InjectMocks
  private InactividadFarmaciaService servicio;

  @Captor
  private ArgumentCaptor<AlertaInactividadDto> alertaCaptor;

  @Captor
  private ArgumentCaptor<LocalDateTime> limiteCaptor;

  @BeforeEach
  void transaccionPasante() {
    // Ejecuta el callback directo: aquí se prueba la lógica, no la transacción real.
    lenient().when(transaccion.execute(any())).thenAnswer(
        inv -> inv.<TransactionCallback<?>>getArgument(0).doInTransaction(null));
  }

  private void farmaciasInactivas(Integer... ids) {
    doReturn(List.of(ids)).when(inactividadRepository).farmaciasParaAlertar(any(LocalDateTime.class));
  }

  @Test
  void sinFarmaciasInactivasNoHaceNada() {
    farmaciasInactivas();

    RevisionInactividadDto resultado = servicio.revisar();

    assertThat(resultado).isEqualTo(new RevisionInactividadDto(0, 0));
    verifyNoInteractions(notificador);
  }

  @Test
  void buscaFarmaciasSinActividadDesdeHace30Dias() {
    LocalDateTime antes = LocalDateTime.now().minusDays(InactividadFarmaciaService.DIAS_SIN_ACTUALIZAR)
        .minusSeconds(1);

    servicio.revisar();

    LocalDateTime despues = LocalDateTime.now().minusDays(InactividadFarmaciaService.DIAS_SIN_ACTUALIZAR);
    verify(inactividadRepository).farmaciasParaAlertar(limiteCaptor.capture());
    assertThat(limiteCaptor.getValue()).isBetween(antes, despues);
  }

  @Test
  void creaLaAlertaYAvisaSinSuspenderCuandoHayMenosDeTresPendientes() {
    farmaciasInactivas(9002);
    doReturn(1L).when(inactividadRepository).contarPendientes(9002);

    RevisionInactividadDto resultado = servicio.revisar();

    assertThat(resultado).isEqualTo(new RevisionInactividadDto(1, 0));
    verify(inactividadRepository).crearAlerta(eq(9002), any(LocalDateTime.class));
    verify(inactividadRepository, never()).suspender(any());
    verify(inactividadRepository, never()).registrarAuditoriaSuspension(any(), any());

    verify(notificador).alertaInactividad(alertaCaptor.capture());
    AlertaInactividadDto aviso = alertaCaptor.getValue();
    assertThat(aviso.farmaciaId()).isEqualTo(9002);
    assertThat(aviso.alertasPendientes()).isEqualTo(1L);
    assertThat(aviso.alertasParaSuspender()).isEqualTo(InactividadFarmaciaService.ALERTAS_PARA_SUSPENDER);
    assertThat(aviso.suspendida()).isFalse();
  }

  @Test
  void suspendeYAuditaCuandoLlegaALaTerceraAlerta() {
    farmaciasInactivas(9002);
    doReturn(3L).when(inactividadRepository).contarPendientes(9002);
    doReturn(1).when(inactividadRepository).suspender(9002);

    RevisionInactividadDto resultado = servicio.revisar();

    assertThat(resultado).isEqualTo(new RevisionInactividadDto(1, 1));
    verify(inactividadRepository).registrarAuditoriaSuspension(eq(9002), any(LocalDateTime.class));

    verify(notificador).alertaInactividad(alertaCaptor.capture());
    assertThat(alertaCaptor.getValue().suspendida()).isTrue();
    assertThat(alertaCaptor.getValue().alertasPendientes()).isEqualTo(3L);
  }

  @Test
  void noAuditaSiLaFarmaciaYaNoEstabaAprobada() {
    // El UPDATE no afecta filas (p. ej. ya suspendida): sin cambio de estado no hay auditoría.
    farmaciasInactivas(9002);
    doReturn(3L).when(inactividadRepository).contarPendientes(9002);
    doReturn(0).when(inactividadRepository).suspender(9002);

    RevisionInactividadDto resultado = servicio.revisar();

    assertThat(resultado).isEqualTo(new RevisionInactividadDto(1, 0));
    verify(inactividadRepository, never()).registrarAuditoriaSuspension(any(), any());
    verify(notificador).alertaInactividad(alertaCaptor.capture());
    assertThat(alertaCaptor.getValue().suspendida()).isFalse();
  }

  @Test
  void siUnaFarmaciaFallaLasDemasSeProcesanIgual() {
    farmaciasInactivas(9001, 9002);
    doThrow(new IllegalStateException("fallo simulado")).when(inactividadRepository)
        .crearAlerta(eq(9001), any(LocalDateTime.class));
    doReturn(1L).when(inactividadRepository).contarPendientes(9002);

    RevisionInactividadDto resultado = servicio.revisar();

    assertThat(resultado).isEqualTo(new RevisionInactividadDto(1, 0));
    // A la farmacia que falló no se le avisa de una alerta que no quedó guardada.
    verify(notificador, times(1)).alertaInactividad(alertaCaptor.capture());
    assertThat(alertaCaptor.getValue().farmaciaId()).isEqualTo(9002);
  }

  @Test
  void noAvisaSiLaTransaccionNoDevuelveResultado() {
    farmaciasInactivas(9002);
    // doReturn evita invocar el stub pasante del @BeforeEach mientras se redefine.
    doReturn(null).when(transaccion).execute(any());

    RevisionInactividadDto resultado = servicio.revisar();

    assertThat(resultado).isEqualTo(new RevisionInactividadDto(0, 0));
    verifyNoInteractions(notificador);
  }
}