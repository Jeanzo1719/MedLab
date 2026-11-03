package com.medlab.main.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la normalización de los filtros del buscador (HU-3). No usan Spring ni base de
 * datos: solo comprueban que los encabezados de texto se conviertan en filtros seguros.
 */
class BusquedaMedicamentosDtoTest {

  @Test
  void normalizaTextoCampoPreciosYOrden() {
    BusquedaMedicamentosDto filtros = BusquedaMedicamentosDto.desde("  ibu ",
        "principio", "  Farmatodo ", "3000", "1000", "PRECIO", "2", "10");

    assertEquals("ibu", filtros.texto());
    assertFalse(filtros.buscarNombre());
    assertTrue(filtros.buscarPrincipio());
    assertEquals("Farmatodo", filtros.farmacia());
    // El rango invertido se corrige solo.
    assertEquals(new BigDecimal("1000"), filtros.precioMin());
    assertEquals(new BigDecimal("3000"), filtros.precioMax());
    assertEquals(BusquedaMedicamentosDto.ORDEN_PRECIO, filtros.orden());
    assertEquals(2, filtros.pagina());
    assertEquals(10, filtros.tamano());
    assertEquals(20, filtros.desplazamiento());
  }

  @Test
  void descartaTextosMuyCortosYValoresInvalidos() {
    BusquedaMedicamentosDto filtros = BusquedaMedicamentosDto.desde("a", "otro", "", "-5",
        "no-es-precio", "otro-orden", "-3", "999");

    assertEquals("", filtros.texto());
    assertTrue(filtros.buscarNombre());
    assertTrue(filtros.buscarPrincipio());
    assertEquals("", filtros.farmacia());
    assertNull(filtros.precioMin());
    assertNull(filtros.precioMax());
    assertEquals(BusquedaMedicamentosDto.ORDEN_RELEVANCIA, filtros.orden());
    assertEquals(0, filtros.pagina());
    assertEquals(BusquedaMedicamentosDto.TAMANO_MAXIMO, filtros.tamano());
    assertTrue(filtros.sinCriterios());
  }

  @Test
  void campoNombreSoloBuscaEnElNombreYCampoTodosBuscaEnAmbos() {
    BusquedaMedicamentosDto nombre = BusquedaMedicamentosDto.desde("dolex", "nombre", null, null,
        null, null, null, null);
    assertTrue(nombre.buscarNombre());
    assertFalse(nombre.buscarPrincipio());

    BusquedaMedicamentosDto todos = BusquedaMedicamentosDto.desde("dolex", "todos", null, null,
        null, null, null, null);
    assertTrue(todos.buscarNombre());
    assertTrue(todos.buscarPrincipio());
  }

  @Test
  void unSoloPrecioDejaElOtroExtremoLibre() {
    BusquedaMedicamentosDto filtros = BusquedaMedicamentosDto.desde(null, null, null, "5000",
        null, null, null, null);

    assertEquals(new BigDecimal("5000"), filtros.precioMin());
    assertNull(filtros.precioMax());
    assertFalse(filtros.sinCriterios());
  }
}
