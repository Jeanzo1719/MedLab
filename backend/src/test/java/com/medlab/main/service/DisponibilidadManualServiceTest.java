package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.medlab.main.dto.DisponibilidadFarmaciaDto;
import com.medlab.main.dto.GuardarDisponibilidadDto;
import com.medlab.main.dto.GuardarDisponibilidadManualDto;
import com.medlab.main.exception.DatosInvalidosException;

/**
 * Pruebas de DisponibilidadManualService sin base de datos. Comprueban que el reporte manual
 * reutiliza el guardado de HU-11 con el id del medicamento encontrado o creado, y que si algo
 * falla no se toca el inventario o el error llega intacto a quien llamó.
 *
 * El deshacer del medicamento recién creado lo hace @Transactional; verlo funcionar requiere una
 * prueba con base de datos y no se cubre aquí.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DisponibilidadManualServiceTest {

  private static final int FARMACIA_ID = 9001;
  private static final int MEDICAMENTO_ID = 7;
  private static final BigDecimal PRECIO = new BigDecimal("12500.50");

  @Mock
  private MedicamentoCatalogoService catalogoService;
  @Mock
  private DisponibilidadService disponibilidadService;

  private DisponibilidadManualService servicio;
  private GuardarDisponibilidadManualDto datos;

  @BeforeEach
  void preparar() {
    servicio = new DisponibilidadManualService(catalogoService, disponibilidadService);
    datos = new GuardarDisponibilidadManualDto("Advil", "Ibuprofeno", 1, "400 mg x 10", 1, 20, PRECIO,
        "bajo_pedido");
  }

  @Test
  void guardarDelegaEnElMismoGuardadoDeHU11ConElIdDelMedicamento() {
    DisponibilidadFarmaciaDto esperado = mock(DisponibilidadFarmaciaDto.class);
    when(catalogoService.buscarOCrear(datos)).thenReturn(MEDICAMENTO_ID);
    when(disponibilidadService.guardar(eq(FARMACIA_ID), any(GuardarDisponibilidadDto.class)))
        .thenReturn(esperado);

    DisponibilidadFarmaciaDto resultado = servicio.guardar(FARMACIA_ID, datos);

    assertSame(esperado, resultado);
    ArgumentCaptor<GuardarDisponibilidadDto> enviado = ArgumentCaptor.forClass(GuardarDisponibilidadDto.class);
    verify(disponibilidadService).guardar(eq(FARMACIA_ID), enviado.capture());
    assertEquals(new GuardarDisponibilidadDto(MEDICAMENTO_ID, 20, PRECIO, "bajo_pedido"), enviado.getValue());
  }

  @Test
  void siElMedicamentoEsInvalidoNoSeTocaElInventario() {
    doThrow(new DatosInvalidosException("La categoría 99 no existe")).when(catalogoService).buscarOCrear(datos);

    assertThrows(DatosInvalidosException.class, () -> servicio.guardar(FARMACIA_ID, datos));

    verifyNoInteractions(disponibilidadService);
  }

  @Test
  void siElGuardadoFallaElErrorSePropagaSinCambios() {
    when(catalogoService.buscarOCrear(datos)).thenReturn(MEDICAMENTO_ID);
    doThrow(new DatosInvalidosException("estado incoherente")).when(disponibilidadService)
        .guardar(eq(FARMACIA_ID), any(GuardarDisponibilidadDto.class));

    DatosInvalidosException error = assertThrows(DatosInvalidosException.class,
        () -> servicio.guardar(FARMACIA_ID, datos));

    assertEquals("estado incoherente", error.getMessage());
  }
}