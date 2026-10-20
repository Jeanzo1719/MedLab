package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medlab.main.dto.DisponibilidadReporteDto;
import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.dto.TendenciaDisponibilidad;
import com.medlab.main.entity.CategoriasEntity;
import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.entity.FormasFarmaceuticasEntity;
import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.exception.MedicamentoNotFoundException;
import com.medlab.main.repository.CalificacionesRepository;
import com.medlab.main.repository.CalificacionesRepository.CalificacionReciente;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.MedicamentosRepository;
import com.medlab.main.service.impl.MedicamentoDetalleServiceImpl;
import com.medlab.main.service.impl.MensajeDisponibilidadServiceImpl;
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

/**
 * Pruebas unitarias de MedicamentoDetalleServiceImpl. Los repositorios y el servicio de
 * tiempo relativo se simulan con Mockito, así que no usan base de datos. Comprueban el caso
 * de medicamento inexistente, el medicamento sin reportes y el armado de los reportes con su
 * tendencia de disponibilidad.
 *
 * Los objetos simulados (medicamento y reportes) se crean en variables antes de usarlos
 * dentro de un when(...), porque Mockito no permite crear y configurar un mock dentro de la
 * configuración de otro.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MedicamentoDetalleServiceTest {

  private static final long ID_MEDICAMENTO = 7;
  private static final String TEXTO_RELATIVO = "hace 2 horas";

  @Mock
  private MedicamentosRepository medicamentosRepository;
  @Mock
  private DisponibilidadRepository disponibilidadRepository;
  @Mock
  private CalificacionesRepository calificacionesRepository;
  @Mock
  private TiempoRelativoService tiempoRelativoService;

  private MedicamentoDetalleService servicio;

  @BeforeEach
  void preparar() {
    servicio = new MedicamentoDetalleServiceImpl(medicamentosRepository, disponibilidadRepository,
        calificacionesRepository, new MensajeDisponibilidadServiceImpl(), tiempoRelativoService);
    when(tiempoRelativoService.getTiempoRelativo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(TEXTO_RELATIVO);
  }

  @Test
  void medicamentoInexistenteLanzaExcepcion() {
    when(medicamentosRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(MedicamentoNotFoundException.class, () -> servicio.getMedicamentoDetalleById(99L));

    verify(disponibilidadRepository, never()).findByMedicamentoIdOrderByFechaHoraReporteDesc(anyLong());
  }

  @Test
  void sinReportesDevuelveListaVaciaYNoConsultaCalificaciones() {
    MedicamentosEntity medicamento = medicamento();
    when(medicamentosRepository.findById(ID_MEDICAMENTO))
        .thenReturn(Optional.of(medicamento));
    when(disponibilidadRepository.findByMedicamentoIdOrderByFechaHoraReporteDesc(ID_MEDICAMENTO))
        .thenReturn(List.of());

    MedicamentoDetalleDto detalle = servicio.getMedicamentoDetalleById(ID_MEDICAMENTO);

    assertEquals("Losartán", detalle.nombreComercial());
    assertEquals("Antihipertensivo", detalle.categoria());
    assertEquals("Tableta", detalle.formaFarmaceutica());
    assertTrue(detalle.reportes().isEmpty());
    verify(calificacionesRepository, never()).findCalificacionesRecientesByDisponibilidad(any(), anyInt());
  }

  @Test
  void reporteConMayoriaSiHayEsDisponible() {
    MedicamentosEntity medicamento = medicamento();
    DisponibilidadEntity laureles = disponibilidad(10, "Laureles");
    when(medicamentosRepository.findById(ID_MEDICAMENTO))
        .thenReturn(Optional.of(medicamento));
    when(disponibilidadRepository.findByMedicamentoIdOrderByFechaHoraReporteDesc(ID_MEDICAMENTO))
        .thenReturn(List.of(laureles));
    when(calificacionesRepository.findCalificacionesRecientesByDisponibilidad(any(), anyInt()))
        .thenReturn(List.of(reciente(10, "Si hay"), reciente(10, "Si hay"),
            reciente(10, "No hay")));

    DisponibilidadReporteDto reporte = servicio.getMedicamentoDetalleById(ID_MEDICAMENTO).reportes().get(0);

    assertEquals("Laureles", reporte.farmacia());
    assertEquals(TEXTO_RELATIVO, reporte.reporteRelativo());
    assertEquals(TendenciaDisponibilidad.DISPONIBLE, reporte.tendencia());
    assertEquals(TendenciaDisponibilidad.DISPONIBLE.getMensaje(),
        reporte.mensajeDisponibilidad());
  }

  @Test
  void variosReportesUsanUnaSolaConsultaYElSinCalificacionesQuedaAparte() {
    MedicamentosEntity medicamento = medicamento();
    DisponibilidadEntity laureles = disponibilidad(10, "Laureles");
    DisponibilidadEntity robledo = disponibilidad(11, "Robledo");
    when(medicamentosRepository.findById(ID_MEDICAMENTO))
        .thenReturn(Optional.of(medicamento));
    when(disponibilidadRepository.findByMedicamentoIdOrderByFechaHoraReporteDesc(ID_MEDICAMENTO))
        .thenReturn(List.of(laureles, robledo));
    when(calificacionesRepository.findCalificacionesRecientesByDisponibilidad(any(), anyInt()))
        .thenReturn(List.of(reciente(10, "Bajo pedido")));

    List<DisponibilidadReporteDto> reportes = servicio.getMedicamentoDetalleById(ID_MEDICAMENTO).reportes();

    assertEquals(TendenciaDisponibilidad.BAJO_PEDIDO, reportes.get(0).tendencia());
    assertEquals(TendenciaDisponibilidad.SIN_CALIFICACIONES, reportes.get(1).tendencia());
    assertEquals("sin calificaciones aún", reportes.get(1).mensajeDisponibilidad());
    verify(calificacionesRepository, times(1)).findCalificacionesRecientesByDisponibilidad(
        eq(List.of(10, 11)), eq(MensajeDisponibilidadService.CALIFICACIONES_RECIENTES));
  }

  private MedicamentosEntity medicamento() {
    CategoriasEntity categoria = mock(CategoriasEntity.class);
    when(categoria.getNombre()).thenReturn("Antihipertensivo");
    FormasFarmaceuticasEntity forma = mock(FormasFarmaceuticasEntity.class);
    when(forma.getNombre()).thenReturn("Tableta");

    MedicamentosEntity medicamento = mock(MedicamentosEntity.class);
    when(medicamento.getId()).thenReturn(ID_MEDICAMENTO);
    when(medicamento.getNombreComercial()).thenReturn("Losartán");
    when(medicamento.getPrincipioActivo()).thenReturn("Losartán potásico");
    when(medicamento.getCategoria()).thenReturn(categoria);
    when(medicamento.getPresentacion()).thenReturn("50 mg");
    when(medicamento.getFormaFarmaceutica()).thenReturn(forma);
    return medicamento;
  }

  private DisponibilidadEntity disponibilidad(int id, String nombreFarmacia) {
    FarmaciasEntity farmacia = mock(FarmaciasEntity.class);
    when(farmacia.getNombreFarmacia()).thenReturn(nombreFarmacia);

    DisponibilidadEntity disponibilidad = mock(DisponibilidadEntity.class);
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
