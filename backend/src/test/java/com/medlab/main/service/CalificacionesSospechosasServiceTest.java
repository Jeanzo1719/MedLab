package com.medlab.main.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medlab.main.dto.DisponibilidadSospechosaDto;
import com.medlab.main.repository.HistorialAdminRepository;
import com.medlab.main.repository.HistorialAdminRepository.DisponibilidadSospechosa;

@ExtendWith(MockitoExtension.class)
class CalificacionesSospechosasServiceTest {

  private static final String TIPO_NO_HAY = "No hay";
  private static final int TODAS = 0;
  private static final int MAXIMO_LISTA = 100;

  @Mock
  private HistorialAdminRepository historialRepository;

  @Mock
  private NotificadorAlertas notificador;

  @InjectMocks
  private CalificacionesSospechosasService servicio;

  private DisponibilidadSospechosa sospechosa(long noHay, long revisadas) {
    DisponibilidadSospechosa d = mock(DisponibilidadSospechosa.class);
    when(d.getFarmacia()).thenReturn("Farmacia Centro");
    when(d.getMedicamento()).thenReturn("Dolex");
    when(d.getCalificacionesNoHay()).thenReturn(noHay);
    when(d.getCalificacionesRevisadas()).thenReturn(revisadas);
    return d;
  }

  @Test
  void listarConsultaTodasLasPublicacionesConVentanaYUmbral() {
    // El mock va en una variable: Mockito no admite stubbing anidado dentro de thenReturn.
    DisponibilidadSospechosa fila = sospechosa(18, 20);
    when(historialRepository.buscarSospechosas(TODAS, CalificacionesSospechosasService.VENTANA,
        CalificacionesSospechosasService.UMBRAL_NO_HAY, TIPO_NO_HAY, MAXIMO_LISTA))
        .thenReturn(List.of(fila));

    List<DisponibilidadSospechosaDto> lista = servicio.listar();

    assertThat(lista).hasSize(1);
    assertThat(lista.get(0).farmacia()).isEqualTo("Farmacia Centro");
    assertThat(lista.get(0).medicamento()).isEqualTo("Dolex");
    assertThat(lista.get(0).calificacionesNoHay()).isEqualTo(18L);
    assertThat(lista.get(0).calificacionesRevisadas()).isEqualTo(20L);
  }

  @Test
  void listarDevuelveVacioSiNadaSuperaElUmbral() {
    when(historialRepository.buscarSospechosas(TODAS, CalificacionesSospechosasService.VENTANA,
        CalificacionesSospechosasService.UMBRAL_NO_HAY, TIPO_NO_HAY, MAXIMO_LISTA))
        .thenReturn(List.of());

    assertThat(servicio.listar()).isEmpty();
  }

  @Test
  void tomaComoCeroLosConteosNulos() {
    // Una agregación sin filas puede llegar como null desde la consulta nativa.
    DisponibilidadSospechosa d = mock(DisponibilidadSospechosa.class);
    doReturn(null).when(d).getCalificacionesNoHay();
    doReturn(null).when(d).getCalificacionesRevisadas();
    when(historialRepository.buscarSospechosas(TODAS, CalificacionesSospechosasService.VENTANA,
        CalificacionesSospechosasService.UMBRAL_NO_HAY, TIPO_NO_HAY, MAXIMO_LISTA))
        .thenReturn(List.of(d));

    DisponibilidadSospechosaDto dto = servicio.listar().get(0);

    assertThat(dto.calificacionesNoHay()).isZero();
    assertThat(dto.calificacionesRevisadas()).isZero();
  }

  @Test
  void revisarDisponibilidadAvisaAlAdminSiSuperaElUmbral() {
    DisponibilidadSospechosa fila = sospechosa(15, 20);
    when(historialRepository.buscarSospechosas(1, CalificacionesSospechosasService.VENTANA,
        CalificacionesSospechosasService.UMBRAL_NO_HAY, TIPO_NO_HAY, MAXIMO_LISTA))
        .thenReturn(List.of(fila));

    servicio.revisarDisponibilidad(1);

    ArgumentCaptor<DisponibilidadSospechosaDto> aviso =
        ArgumentCaptor.forClass(DisponibilidadSospechosaDto.class);
    verify(notificador).calificacionesSospechosas(aviso.capture());
    assertThat(aviso.getValue().calificacionesNoHay()).isEqualTo(15L);
  }

  @Test
  void revisarDisponibilidadNoAvisaSiNoSuperaElUmbral() {
    when(historialRepository.buscarSospechosas(1, CalificacionesSospechosasService.VENTANA,
        CalificacionesSospechosasService.UMBRAL_NO_HAY, TIPO_NO_HAY, MAXIMO_LISTA))
        .thenReturn(List.of());

    servicio.revisarDisponibilidad(1);

    verifyNoInteractions(notificador);
  }
}