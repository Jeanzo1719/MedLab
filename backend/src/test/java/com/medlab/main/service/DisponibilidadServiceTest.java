package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.medlab.main.dto.DisponibilidadFarmaciaDto;
import com.medlab.main.dto.GuardarDisponibilidadDto;
import com.medlab.main.dto.ResumenInventarioDto;
import com.medlab.main.entity.Disponibilidad;
import com.medlab.main.entity.EstadoDisponibilidad;
import com.medlab.main.entity.EstadoFarmacia;
import com.medlab.main.entity.Farmacia;
import com.medlab.main.entity.Medicamento;
import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.exception.RecursoNoEncontradoException;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.EstadoDisponibilidadRepository;
import com.medlab.main.repository.FarmaciaRepository;
import com.medlab.main.repository.MedicamentoRepository;

/**
 * Pruebas de DisponibilidadService sin base de datos. Se centran en las reglas que la BD no
 * hace cumplir sola: coherencia entre estado y cantidad, farmacia aprobada, límites de la
 * paginación y que un aviso en vivo fallido nunca deshaga un guardado.
 *
 * Las filas de disponibilidad no tienen setters, así que se simulan con Mockito. Cada mock
 * se guarda en una variable antes de usarlo dentro de un when(...).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisponibilidadServiceTest {

  private static final int FARMACIA_ID = 9001;
  private static final int MEDICAMENTO_ID = 1;
  private static final int ID_ESTADO = 3;
  private static final BigDecimal PRECIO = new BigDecimal("5500.00");

  @Mock
  private DisponibilidadRepository disponibilidadRepository;
  @Mock
  private EstadoDisponibilidadRepository estadoDisponibilidadRepository;
  @Mock
  private FarmaciaRepository farmaciaRepository;
  @Mock
  private MedicamentoRepository medicamentoRepository;
  @Mock
  private MedicamentoDetalleNotificador notificador;

  private DisponibilidadService servicio;

  @BeforeEach
  void preparar() {
    servicio = new DisponibilidadService(disponibilidadRepository, estadoDisponibilidadRepository,
        farmaciaRepository, medicamentoRepository, notificador);
    // Caso normal por defecto: farmacia aprobada y medicamento existente.
    when(farmaciaRepository.findById(FARMACIA_ID)).thenReturn(Optional.of(farmacia("aprobada")));
    when(medicamentoRepository.existsById(MEDICAMENTO_ID)).thenReturn(true);
  }

  @Test
  void guardarGuardaYDevuelveElProductoConSuEstado() {
    prepararGuardado(10, EstadoDisponibilidad.DISPONIBLE);

    DisponibilidadFarmaciaDto dto = servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(MEDICAMENTO_ID, 10, PRECIO, null));

    verify(disponibilidadRepository).guardar(FARMACIA_ID, MEDICAMENTO_ID, 10, PRECIO, ID_ESTADO);
    assertEquals("Dolex", dto.nombreComercial());
    assertEquals(10, dto.cantidadDisponible());
    assertEquals(EstadoDisponibilidad.DISPONIBLE, dto.estado());
    verify(notificador).publicar(MEDICAMENTO_ID);
  }

  @Test
  void sinEstadoYConCantidadCeroSeGuardaComoAgotado() {
    prepararGuardado(0, EstadoDisponibilidad.AGOTADO);

    servicio.guardar(FARMACIA_ID, new GuardarDisponibilidadDto(MEDICAMENTO_ID, 0, PRECIO, null));

    verify(estadoDisponibilidadRepository).findByNombre(EstadoDisponibilidad.AGOTADO);
    verify(disponibilidadRepository).guardar(FARMACIA_ID, MEDICAMENTO_ID, 0, PRECIO, ID_ESTADO);
  }

  @Test
  void bajoPedidoAceptaCantidadCero() {
    prepararGuardado(0, EstadoDisponibilidad.BAJO_PEDIDO);

    servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(MEDICAMENTO_ID, 0, PRECIO, "bajo_pedido"));

    verify(disponibilidadRepository).guardar(FARMACIA_ID, MEDICAMENTO_ID, 0, PRECIO, ID_ESTADO);
  }

  @Test
  void disponibleConCantidadCeroSeRechaza() {
    assertThrows(DatosInvalidosException.class, () -> servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(MEDICAMENTO_ID, 0, PRECIO, "disponible")));

    verify(disponibilidadRepository, never()).guardar(any(), any(), any(), any(), any());
  }

  @Test
  void agotadoConCantidadMayorACeroSeRechaza() {
    assertThrows(DatosInvalidosException.class, () -> servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(MEDICAMENTO_ID, 4, PRECIO, "agotado")));

    verify(disponibilidadRepository, never()).guardar(any(), any(), any(), any(), any());
  }

  @Test
  void estadoDesconocidoSeRechaza() {
    assertThrows(DatosInvalidosException.class, () -> servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(MEDICAMENTO_ID, 4, PRECIO, "roto")));
  }

  @Test
  void medicamentoInexistenteLanzaNoEncontrado() {
    when(medicamentoRepository.existsById(99)).thenReturn(false);

    assertThrows(RecursoNoEncontradoException.class, () -> servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(99, 5, PRECIO, null)));

    verify(disponibilidadRepository, never()).guardar(any(), any(), any(), any(), any());
  }

  @Test
  void farmaciaPendienteNoPuedeTocarElInventario() {
    when(farmaciaRepository.findById(FARMACIA_ID)).thenReturn(Optional.of(farmacia("pendiente")));

    assertThrows(AccesoPanelDenegadoException.class, () -> servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(MEDICAMENTO_ID, 5, PRECIO, null)));
    assertThrows(AccesoPanelDenegadoException.class, () -> servicio.resumen(FARMACIA_ID));

    verify(disponibilidadRepository, never()).guardar(any(), any(), any(), any(), any());
  }

  @Test
  void farmaciaInexistenteSeRechaza() {
    when(farmaciaRepository.findById(FARMACIA_ID)).thenReturn(Optional.empty());

    assertThrows(AccesoPanelDenegadoException.class, () -> servicio.resumen(FARMACIA_ID));
  }

  @Test
  void stockBajoSoloEntreUnoYElUmbral() {
    assertFalse(stockBajoCon(0));
    assertTrue(stockBajoCon(1));
    assertTrue(stockBajoCon(DisponibilidadService.UMBRAL_STOCK_BAJO - 1));
    assertFalse(stockBajoCon(DisponibilidadService.UMBRAL_STOCK_BAJO));
  }

  @Test
  void siFallaElAvisoEnVivoElGuardadoSigueSiendoValido() {
    prepararGuardado(10, EstadoDisponibilidad.DISPONIBLE);
    doThrow(new RuntimeException("sin conexión")).when(notificador).publicar(MEDICAMENTO_ID);

    DisponibilidadFarmaciaDto dto = servicio.guardar(FARMACIA_ID,
        new GuardarDisponibilidadDto(MEDICAMENTO_ID, 10, PRECIO, null));

    assertEquals(10, dto.cantidadDisponible());
  }

  @Test
  void marcarAgotadoAvisaElCambio() {
    EstadoDisponibilidad agotado = estado(EstadoDisponibilidad.AGOTADO);
    when(estadoDisponibilidadRepository.findByNombre(EstadoDisponibilidad.AGOTADO))
        .thenReturn(Optional.of(agotado));
    when(disponibilidadRepository.marcarAgotado(FARMACIA_ID, MEDICAMENTO_ID, ID_ESTADO)).thenReturn(1);

    servicio.marcarAgotado(FARMACIA_ID, MEDICAMENTO_ID);

    verify(notificador).publicar(MEDICAMENTO_ID);
  }

  @Test
  void marcarAgotadoDeUnProductoQueNoManejaLanzaNoEncontrado() {
    EstadoDisponibilidad agotado = estado(EstadoDisponibilidad.AGOTADO);
    when(estadoDisponibilidadRepository.findByNombre(EstadoDisponibilidad.AGOTADO))
        .thenReturn(Optional.of(agotado));
    when(disponibilidadRepository.marcarAgotado(FARMACIA_ID, MEDICAMENTO_ID, ID_ESTADO)).thenReturn(0);

    assertThrows(RecursoNoEncontradoException.class,
        () -> servicio.marcarAgotado(FARMACIA_ID, MEDICAMENTO_ID));

    verify(notificador, never()).publicar(any());
  }

  @Test
  void resumenTraLosTresEstadosAunqueAlgunoEsteEnCero() {
    DisponibilidadRepository.ConteoEstado conteo = mock(DisponibilidadRepository.ConteoEstado.class);
    when(conteo.getEstado()).thenReturn(EstadoDisponibilidad.DISPONIBLE);
    when(conteo.getTotal()).thenReturn(2L);
    when(disponibilidadRepository.contarPorEstado(FARMACIA_ID)).thenReturn(List.of(conteo));

    ResumenInventarioDto resumen = servicio.resumen(FARMACIA_ID);

    assertEquals(3, resumen.porEstado().size());
    assertEquals(2L, resumen.porEstado().get(EstadoDisponibilidad.DISPONIBLE));
    assertEquals(0L, resumen.porEstado().get(EstadoDisponibilidad.AGOTADO));
    assertEquals(0L, resumen.porEstado().get(EstadoDisponibilidad.BAJO_PEDIDO));
    assertEquals(2L, resumen.total());
  }

  @Test
  void listarLimpiaElTextoYLimitaLaPagina() {
    Page<Disponibilidad> vacia = new PageImpl<>(List.of());
    when(disponibilidadRepository.buscarPorFarmacia(eq(FARMACIA_ID), eq("dol"), any(Pageable.class)))
        .thenReturn(vacia);

    servicio.listar(FARMACIA_ID, "  dol ", -1, 500);

    ArgumentCaptor<Pageable> paginacion = ArgumentCaptor.forClass(Pageable.class);
    verify(disponibilidadRepository).buscarPorFarmacia(eq(FARMACIA_ID), eq("dol"), paginacion.capture());
    assertEquals(0, paginacion.getValue().getPageNumber());
    assertEquals(50, paginacion.getValue().getPageSize());
    assertNotNull(paginacion.getValue().getSort().getOrderFor("medicamento.nombreComercial"));
  }

  /** Deja listo todo lo que guardar() necesita para devolver el producto ya guardado. */
  private void prepararGuardado(int cantidad, String nombreEstado) {
    EstadoDisponibilidad estado = estado(nombreEstado);
    when(estadoDisponibilidadRepository.findByNombre(nombreEstado)).thenReturn(Optional.of(estado));
    Disponibilidad fila = fila(cantidad, estado);
    when(disponibilidadRepository.findByFarmaciaUsuarioIdAndMedicamentoId(FARMACIA_ID, MEDICAMENTO_ID))
        .thenReturn(Optional.of(fila));
  }

  private boolean stockBajoCon(int cantidad) {
    String nombreEstado = cantidad == 0 ? EstadoDisponibilidad.AGOTADO : EstadoDisponibilidad.DISPONIBLE;
    prepararGuardado(cantidad, nombreEstado);
    return servicio.guardar(FARMACIA_ID, new GuardarDisponibilidadDto(MEDICAMENTO_ID, cantidad, PRECIO, null))
        .stockBajo();
  }

  private Farmacia farmacia(String nombreEstado) {
    EstadoFarmacia estado = new EstadoFarmacia();
    estado.setNombre(nombreEstado);
    Farmacia farmacia = new Farmacia();
    farmacia.setUsuarioId(FARMACIA_ID);
    farmacia.setEstadoAprobacion(estado);
    return farmacia;
  }

  private EstadoDisponibilidad estado(String nombre) {
    EstadoDisponibilidad estado = mock(EstadoDisponibilidad.class);
    when(estado.getId()).thenReturn(ID_ESTADO);
    when(estado.getNombre()).thenReturn(nombre);
    return estado;
  }

  private Disponibilidad fila(int cantidad, EstadoDisponibilidad estado) {
    Medicamento medicamento = new Medicamento();
    medicamento.setId(MEDICAMENTO_ID);
    medicamento.setNombreComercial("Dolex");
    medicamento.setPrincipioActivo("Acetaminofen");
    medicamento.setPresentacion("500 mg x 10");

    Disponibilidad fila = mock(Disponibilidad.class);
    when(fila.getMedicamento()).thenReturn(medicamento);
    when(fila.getCantidadDisponible()).thenReturn(cantidad);
    when(fila.getPrecio()).thenReturn(PRECIO);
    when(fila.getEstadoDisponibilidad()).thenReturn(estado);
    when(fila.getFechaHoraReporte()).thenReturn(LocalDateTime.of(2026, 10, 23, 15, 0));
    return fila;
  }
}