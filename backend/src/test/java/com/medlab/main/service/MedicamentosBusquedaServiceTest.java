package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medlab.main.dto.BusquedaMedicamentosDto;
import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.dto.PaginaMedicamentosDto;
import com.medlab.main.repository.CategoriasRepository;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.FormasFarmaceuticasRepository;
import com.medlab.main.repository.MedicamentosRepository;
import com.medlab.main.repository.MedicamentosRepository.MedicamentoBusquedaProyeccion;
import com.medlab.main.service.impl.MedicamentosServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * Pruebas de la búsqueda combinada de medicamentos (HU-3). El repositorio se simula con
 * Mockito, así que no se usa base de datos. Comprueban que sin criterios no se consulte y que
 * la página y los conteos se armen bien.
 */
@ExtendWith(MockitoExtension.class)
class MedicamentosBusquedaServiceTest {

  @Mock
  private MedicamentosRepository medicamentosRepository;
  @Mock
  private DisponibilidadRepository disponibilidadRepository;
  @Mock
  private CategoriasRepository categoriasRepository;
  @Mock
  private FormasFarmaceuticasRepository formasFarmaceuticasRepository;

  private MedicamentosService servicio;

  @BeforeEach
  void preparar() {
    servicio = new MedicamentosServiceImpl(medicamentosRepository, disponibilidadRepository,
        categoriasRepository, formasFarmaceuticasRepository);
  }

  @Test
  void sinCriteriosDevuelvePaginaVaciaSinConsultar() {
    BusquedaMedicamentosDto filtros = BusquedaMedicamentosDto.desde(null, null, null, null, null,
        null, null, null);

    PaginaMedicamentosDto pagina = servicio.buscarMedicamentos(filtros);

    assertTrue(pagina.resultados().isEmpty());
    assertEquals(0, pagina.total());
    assertEquals(0, pagina.totalPaginas());
    verify(medicamentosRepository, never()).buscarMedicamentos(any(), any());
  }

  @Test
  void convierteLaProyeccionYArmaLaPaginacion() {
    BusquedaMedicamentosDto filtros = BusquedaMedicamentosDto.desde("dolex", null, null, null,
        null, "precio", "1", "2");
    PageRequest paginaPedida = PageRequest.of(1, 2);
    when(medicamentosRepository.buscarMedicamentos(filtros, paginaPedida))
        .thenReturn(new PageImpl<>(List.of(proyeccion()), paginaPedida, 5));

    PaginaMedicamentosDto pagina = servicio.buscarMedicamentos(filtros);

    assertEquals(1, pagina.resultados().size());
    MedicamentoBusquedaDto resultado = pagina.resultados().get(0);
    assertEquals(9L, resultado.id());
    assertEquals("Dolex", resultado.nombreComercial());
    assertEquals("Acetaminofén", resultado.principioActivo());
    assertEquals("Tableta", resultado.formaFarmaceutica());
    assertEquals("Analgésico", resultado.categoria());
    assertEquals(new BigDecimal("4500.00"), resultado.precioMinimo());
    assertEquals(3L, resultado.farmaciasDisponibles());
    assertEquals(4L, resultado.farmaciasReportaron());
    assertEquals(5, pagina.total());
    assertEquals(1, pagina.pagina());
    assertEquals(2, pagina.tamano());
    assertEquals(3, pagina.totalPaginas());
    assertTrue(pagina.hayMas());
  }

  @Test
  void losConteosNulosSeConviertenEnCero() {
    BusquedaMedicamentosDto filtros = BusquedaMedicamentosDto.desde("dolex", null, null, null,
        null, null, null, null);
    PageRequest paginaPedida = PageRequest.of(0, BusquedaMedicamentosDto.TAMANO_POR_DEFECTO);
    when(medicamentosRepository.buscarMedicamentos(filtros, paginaPedida))
        .thenReturn(new PageImpl<>(List.of(proyeccionSinConteos()), paginaPedida, 1));

    MedicamentoBusquedaDto resultado = servicio.buscarMedicamentos(filtros).resultados().get(0);

    assertNull(resultado.precioMinimo());
    assertEquals(0L, resultado.farmaciasDisponibles());
    assertEquals(0L, resultado.farmaciasReportaron());
  }

  private MedicamentoBusquedaProyeccion proyeccion() {
    return proyeccion(3L, 4L, new BigDecimal("4500.00"));
  }

  private MedicamentoBusquedaProyeccion proyeccionSinConteos() {
    return proyeccion(null, null, null);
  }

  private MedicamentoBusquedaProyeccion proyeccion(Long disponibles, Long reportaron,
      BigDecimal precioMinimo) {
    MedicamentoBusquedaProyeccion fila = mock(MedicamentoBusquedaProyeccion.class);
    when(fila.getId()).thenReturn(9L);
    when(fila.getNombreComercial()).thenReturn("Dolex");
    when(fila.getPrincipioActivo()).thenReturn("Acetaminofén");
    when(fila.getPresentacion()).thenReturn("500 mg x 10");
    when(fila.getFormaFarmaceutica()).thenReturn("Tableta");
    when(fila.getCategoria()).thenReturn("Analgésico");
    when(fila.getPrecioMinimo()).thenReturn(precioMinimo);
    when(fila.getFarmaciasDisponibles()).thenReturn(disponibles);
    when(fila.getFarmaciasReportaron()).thenReturn(reportaron);
    return fila;
  }
}
