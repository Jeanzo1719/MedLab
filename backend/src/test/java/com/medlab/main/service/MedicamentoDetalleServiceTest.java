package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.medlab.main.dto.DisponibilidadReporteDto;
import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.dto.TendenciaDisponibilidad;
import com.medlab.main.entity.Categoria;
import com.medlab.main.entity.Disponibilidad;
import com.medlab.main.entity.Farmacia;
import com.medlab.main.entity.FormaFarmaceutica;
import com.medlab.main.entity.Medicamento;
import com.medlab.main.exception.MedicamentoNoEncontradoException;
import com.medlab.main.repository.CalificacionRepository;
import com.medlab.main.repository.CalificacionRepository.CalificacionReciente;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.MedicamentoRepository;

/**
 * Pruebas unitarias de MedicamentoDetalleService. Los repositorios y el servicio de tiempo
 * relativo se simulan con Mockito, así que no usan base de datos. Comprueban el caso de
 * medicamento inexistente, el medicamento sin reportes y el armado de los reportes con su
 * tendencia de disponibilidad.
 *
 * Los objetos simulados (medicamento y reportes) se crean en variables antes de usarlos
 * dentro de un when(...), porque Mockito no permite crear y configurar un mock dentro de la
 * configuración de otro.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MedicamentoDetalleServiceTest {

  private static final int ID_MEDICAMENTO = 7;
  private static final String TEXTO_RELATIVO = "hace 2 horas";

  @Mock
  private MedicamentoRepository medicamentoRepository;
  @Mock
  private DisponibilidadRepository disponibilidadRepository;
  @Mock
  private CalificacionRepository calificacionRepository;
  @Mock
  private TiempoRelativoService tiempoRelativoService;

  private MedicamentoDetalleService servicio;

  @BeforeEach
  void preparar() {
    servicio = new MedicamentoDetalleService(medicamentoRepository, disponibilidadRepository,
        calificacionRepository, new MensajeDisponibilidadService(), tiempoRelativoService);
    when(tiempoRelativoService.desde(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(TEXTO_RELATIVO);
  }

  @Test
  void medicamentoInexistenteLanzaExcepcion() {
    when(medicamentoRepository.findDetalleById(99)).thenReturn(Optional.empty());

    assertThrows(MedicamentoNoEncontradoException.class, () -> servicio.obtener(99));

    verify(disponibilidadRepository, never()).findByMedicamentoIdConFarmacia(any());
  }

  @Test
  void sinReportesDevuelveListaVaciaYNoConsultaCalificaciones() {
    Medicamento medicamento = medicamento();
    when(medicamentoRepository.findDetalleById(ID_MEDICAMENTO))
        .thenReturn(Optional.of(medicamento));
    when(disponibilidadRepository.findByMedicamentoIdConFarmacia(ID_MEDICAMENTO))
        .thenReturn(List.of());

    MedicamentoDetalleDto detalle = servicio.obtener(ID_MEDICAMENTO);

    assertEquals("Losartán", detalle.nombreComercial());
    assertEquals("Antihipertensivo", detalle.categoria());
    assertEquals("Tableta", detalle.formaFarmaceutica());
    assertTrue(detalle.reportes().isEmpty());
    verify(calificacionRepository, never()).buscarRecientesPorDisponibilidad(any(), anyInt());
  }

  @Test
  void reporteConMayoriaSiHayEsDisponible() {
    Medicamento medicamento = medicamento();
    Disponibilidad laureles = disponibilidad(10, "Laureles");
    when(medicamentoRepository.findDetalleById(ID_MEDICAMENTO))
        .thenReturn(Optional.of(medicamento));
    when(disponibilidadRepository.findByMedicamentoIdConFarmacia(ID_MEDICAMENTO))
        .thenReturn(List.of(laureles));
    when(calificacionRepository.buscarRecientesPorDisponibilidad(any(), anyInt()))
        .thenReturn(List.of(reciente(10, "Si hay"), reciente(10, "Si hay"),
            reciente(10, "No hay")));

    DisponibilidadReporteDto reporte = servicio.obtener(ID_MEDICAMENTO).reportes().get(0);

    assertEquals("Laureles", reporte.farmacia());
    assertEquals(TEXTO_RELATIVO, reporte.reporteRelativo());
    assertEquals(TendenciaDisponibilidad.DISPONIBLE, reporte.tendencia());
    assertEquals(TendenciaDisponibilidad.DISPONIBLE.getMensaje(),
        reporte.mensajeDisponibilidad());
  }

  @Test
  void variosReportesUsanUnaSolaConsultaYElSinCalificacionesQuedaAparte() {
    Medicamento medicamento = medicamento();
    Disponibilidad laureles = disponibilidad(10, "Laureles");
    Disponibilidad robledo = disponibilidad(11, "Robledo");
    when(medicamentoRepository.findDetalleById(ID_MEDICAMENTO))
        .thenReturn(Optional.of(medicamento));
    when(disponibilidadRepository.findByMedicamentoIdConFarmacia(ID_MEDICAMENTO))
        .thenReturn(List.of(laureles, robledo));
    when(calificacionRepository.buscarRecientesPorDisponibilidad(any(), anyInt()))
        .thenReturn(List.of(reciente(10, "Bajo pedido")));

    List<DisponibilidadReporteDto> reportes = servicio.obtener(ID_MEDICAMENTO).reportes();

    assertEquals(TendenciaDisponibilidad.BAJO_PEDIDO, reportes.get(0).tendencia());
    assertEquals(TendenciaDisponibilidad.SIN_CALIFICACIONES, reportes.get(1).tendencia());
    assertEquals("sin calificaciones aún", reportes.get(1).mensajeDisponibilidad());
    verify(calificacionRepository, times(1)).buscarRecientesPorDisponibilidad(
        eq(List.of(10, 11)), eq(MensajeDisponibilidadService.CALIFICACIONES_RECIENTES));
  }

  private Medicamento medicamento() {
    Categoria categoria = mock(Categoria.class);
    when(categoria.getNombre()).thenReturn("Antihipertensivo");
    FormaFarmaceutica forma = mock(FormaFarmaceutica.class);
    when(forma.getNombre()).thenReturn("Tableta");

    Medicamento medicamento = mock(Medicamento.class);
    when(medicamento.getId()).thenReturn(ID_MEDICAMENTO);
    when(medicamento.getNombreComercial()).thenReturn("Losartán");
    when(medicamento.getPrincipioActivo()).thenReturn("Losartán potásico");
    when(medicamento.getCategoria()).thenReturn(categoria);
    when(medicamento.getPresentacion()).thenReturn("50 mg");
    when(medicamento.getFormaFarmaceutica()).thenReturn(forma);
    return medicamento;
  }

  private Disponibilidad disponibilidad(int id, String nombreFarmacia) {
    Farmacia farmacia = mock(Farmacia.class);
    when(farmacia.getNombreFarmacia()).thenReturn(nombreFarmacia);

    Disponibilidad disponibilidad = mock(Disponibilidad.class);
    when(disponibilidad.getId()).thenReturn(id);
    when(disponibilidad.getFarmacia()).thenReturn(farmacia);
    when(disponibilidad.getFechaHoraReporte()).thenReturn(LocalDateTime.of(2026, 10, 16, 8, 0));
    return disponibilidad;
  }

  private CalificacionReciente reciente(int disponibilidadId, String tipo) {
    return new CalificacionReciente() {
      @Override
      public Integer getDisponibilidadId() {
        return disponibilidadId;
      }

      @Override
      public String getTipo() {
        return tipo;
      }
    };
  }
}