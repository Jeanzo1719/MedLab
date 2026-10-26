package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.medlab.main.dto.CatalogosDto;
import com.medlab.main.dto.GuardarDisponibilidadManualDto;
import com.medlab.main.entity.Categoria;
import com.medlab.main.entity.EstadoDisponibilidad;
import com.medlab.main.entity.FormaFarmaceutica;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.repository.CategoriaRepository;
import com.medlab.main.repository.FormaFarmaceuticaRepository;
import com.medlab.main.repository.MedicamentoRepository;

/**
 * Pruebas de MedicamentoCatalogoService sin base de datos. Cubren lo que la BD no resuelve sola:
 * limpiar el texto antes de buscar (para no duplicar), validar que categoría y forma existan y
 * revisar el largo ya limpio, porque el CHECK de la BD cuenta lo que se guarda.
 *
 * Que el INSERT IGNORE no duplique de verdad lo garantiza el UNIQUE de la tabla; eso se
 * comprobó a mano contra la base, no aquí.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MedicamentoCatalogoServiceTest {

  private static final int CATEGORIA_ID = 1;
  private static final int FORMA_ID = 1;
  private static final int MEDICAMENTO_ID = 7;
  private static final BigDecimal PRECIO = new BigDecimal("12500.50");

  @Mock
  private MedicamentoRepository medicamentoRepository;
  @Mock
  private CategoriaRepository categoriaRepository;
  @Mock
  private FormaFarmaceuticaRepository formaFarmaceuticaRepository;

  private MedicamentoCatalogoService servicio;

  @BeforeEach
  void preparar() {
    servicio = new MedicamentoCatalogoService(medicamentoRepository, categoriaRepository,
        formaFarmaceuticaRepository);
    // Caso normal por defecto: categoría y forma existen y el medicamento se encuentra.
    when(categoriaRepository.existsById(CATEGORIA_ID)).thenReturn(true);
    when(formaFarmaceuticaRepository.existsById(FORMA_ID)).thenReturn(true);
    when(medicamentoRepository.buscarIdPorCombinacion(anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(Optional.of(MEDICAMENTO_ID));
  }

  @Test
  void devuelveElIdDelMedicamentoQueYaExisteSinCrearOtro() {
    Integer id = servicio.buscarOCrear(datos("Advil", "Ibuprofeno", CATEGORIA_ID, "400 mg x 10", FORMA_ID));

    assertEquals(MEDICAMENTO_ID, id);
    verify(medicamentoRepository).crearSiNoExiste("Advil", "Ibuprofeno", CATEGORIA_ID, "400 mg x 10", FORMA_ID);
    verify(medicamentoRepository).buscarIdPorCombinacion("Advil", "Ibuprofeno", "400 mg x 10", FORMA_ID);
  }

  @Test
  void limpiaEspaciosSobrantesAntesDeBuscarOCrear() {
    servicio.buscarOCrear(datos("  Dolex   Forte ", " Acetaminofen ", CATEGORIA_ID, " 500 mg   x 10 ", FORMA_ID));

    verify(medicamentoRepository).crearSiNoExiste("Dolex Forte", "Acetaminofen", CATEGORIA_ID,
        "500 mg x 10", FORMA_ID);
    verify(medicamentoRepository).buscarIdPorCombinacion("Dolex Forte", "Acetaminofen", "500 mg x 10", FORMA_ID);
  }

  @Test
  void categoriaInexistenteSeRechazaSinCrearNada() {
    when(categoriaRepository.existsById(99)).thenReturn(false);

    assertThrows(DatosInvalidosException.class,
        () -> servicio.buscarOCrear(datos("Advil", "Ibuprofeno", 99, "400 mg x 10", FORMA_ID)));

    verify(medicamentoRepository, never()).crearSiNoExiste(anyString(), anyString(), anyInt(), anyString(),
        anyInt());
  }

  @Test
  void formaFarmaceuticaInexistenteSeRechazaSinCrearNada() {
    when(formaFarmaceuticaRepository.existsById(99)).thenReturn(false);

    assertThrows(DatosInvalidosException.class,
        () -> servicio.buscarOCrear(datos("Advil", "Ibuprofeno", CATEGORIA_ID, "400 mg x 10", 99)));

    verify(medicamentoRepository, never()).crearSiNoExiste(anyString(), anyString(), anyInt(), anyString(),
        anyInt());
  }

  @Test
  void nombreDemasiadoCortoTrasLimpiarSeRechaza() {
    assertThrows(DatosInvalidosException.class,
        () -> servicio.buscarOCrear(datos("  a ", "Ibuprofeno", CATEGORIA_ID, "400 mg x 10", FORMA_ID)));
  }

  @Test
  void nombreDemasiadoLargoSeRechaza() {
    assertThrows(DatosInvalidosException.class, () -> servicio
        .buscarOCrear(datos("x".repeat(101), "Ibuprofeno", CATEGORIA_ID, "400 mg x 10", FORMA_ID)));
  }

  @Test
  void principioActivoDemasiadoLargoSeRechaza() {
    assertThrows(DatosInvalidosException.class, () -> servicio
        .buscarOCrear(datos("Advil", "x".repeat(151), CATEGORIA_ID, "400 mg x 10", FORMA_ID)));
  }

  @Test
  void presentacionVaciaTrasLimpiarSeRechaza() {
    assertThrows(DatosInvalidosException.class,
        () -> servicio.buscarOCrear(datos("Advil", "Ibuprofeno", CATEGORIA_ID, "   ", FORMA_ID)));
  }

  @Test
  void siElMedicamentoNoApareceTrasCrearseSeAvisaDelError() {
    when(medicamentoRepository.buscarIdPorCombinacion(anyString(), anyString(), anyString(), anyInt()))
        .thenReturn(Optional.empty());

    assertThrows(IllegalStateException.class,
        () -> servicio.buscarOCrear(datos("Advil", "Ibuprofeno", CATEGORIA_ID, "400 mg x 10", FORMA_ID)));
  }

  @Test
  void listarArmaLasOpcionesYLosTresEstados() {
    Categoria categoria = mock(Categoria.class);
    when(categoria.getId()).thenReturn(1);
    when(categoria.getNombre()).thenReturn("Analgesico");
    FormaFarmaceutica forma = mock(FormaFarmaceutica.class);
    when(forma.getId()).thenReturn(2);
    when(forma.getNombre()).thenReturn("cápsula");
    when(categoriaRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(categoria));
    when(formaFarmaceuticaRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(forma));

    CatalogosDto catalogos = servicio.listar();

    assertEquals(1, catalogos.categorias().size());
    assertEquals(1, catalogos.categorias().get(0).id());
    assertEquals("Analgesico", catalogos.categorias().get(0).nombre());
    assertEquals(2, catalogos.formasFarmaceuticas().get(0).id());
    assertEquals("cápsula", catalogos.formasFarmaceuticas().get(0).nombre());
    assertEquals(List.of(EstadoDisponibilidad.DISPONIBLE, EstadoDisponibilidad.AGOTADO,
        EstadoDisponibilidad.BAJO_PEDIDO), catalogos.estados());
  }

  private GuardarDisponibilidadManualDto datos(String nombre, String principio, int categoriaId,
      String presentacion, int formaId) {
    return new GuardarDisponibilidadManualDto(nombre, principio, categoriaId, presentacion, formaId, 20, PRECIO,
        null);
  }
}