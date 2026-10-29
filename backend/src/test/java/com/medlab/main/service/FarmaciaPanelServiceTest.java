package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.medlab.main.dto.DatosFarmaciaDto;
import com.medlab.main.dto.GuardarDatosFarmaciaDto;
import com.medlab.main.entity.EstadosFarmaciaEntity;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.repository.FarmaciasJpaRepository;

/**
 * Pruebas de FarmaciaPanelService sin base de datos. Cubren lo que no se ve leyendo el código:
 * cómo se limpian los textos antes de guardar, que las coordenadas vayan juntas, que la
 * dirección se arme bien y que solo una farmacia aprobada pueda editarse.
 *
 * La farmacia es un objeto real y no un mock: el service la modifica directamente (JPA guarda
 * el cambio al cerrar la transacción), así que basta con mirar sus campos después.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FarmaciaPanelServiceTest {

  private static final int FARMACIA_ID = 9001;
  private static final BigDecimal LATITUD_ORIGINAL = new BigDecimal("6.2442");
  private static final BigDecimal LONGITUD_ORIGINAL = new BigDecimal("-75.5812");
  private static final BigDecimal LATITUD_NUEVA = new BigDecimal("4.7110");
  private static final BigDecimal LONGITUD_NUEVA = new BigDecimal("-74.0721");

  @Mock
  private FarmaciasJpaRepository farmaciaRepository;
  @Mock
  private ActividadFarmaciaService actividadFarmacia;

  private FarmaciaPanelService servicio;
  private FarmaciasEntity farmacia;

  @BeforeEach
  void preparar() {
    servicio = new FarmaciaPanelService(farmaciaRepository, actividadFarmacia);
    farmacia = farmacia("aprobada");
    when(farmaciaRepository.findById(FARMACIA_ID)).thenReturn(Optional.of(farmacia));
  }

  @Test
  void obtenerArmaLaDireccionCompleta() {
    farmacia.setLetraViaPrincipal("A");
    farmacia.setLetraViaGeneradora("B");
    farmacia.setComplemento("Local 3");

    DatosFarmaciaDto dto = servicio.obtener(FARMACIA_ID);

    assertEquals("Calle 10A # 5B - 20, Local 3", dto.direccion());
    assertEquals("aprobada", dto.estado());
  }

  @Test
  void obtenerArmaLaDireccionSinLetrasNiComplemento() {
    DatosFarmaciaDto dto = servicio.obtener(FARMACIA_ID);

    assertEquals("Calle 10 # 5 - 20", dto.direccion());
  }

  @Test
  void actualizarLimpiaEspaciosPoneLetrasEnMayusculaYGuardaVaciosComoNull() {
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("  Farmacia Central ", " Carrera ",
        " 15 ", " a ", " 7 ", "", " 30 ", "   ", "", null, null);

    DatosFarmaciaDto dto = servicio.actualizar(FARMACIA_ID, datos);

    assertEquals("Farmacia Central", farmacia.getNombreFarmacia());
    assertEquals("Carrera", farmacia.getTipoViaPrincipal());
    assertEquals("15", farmacia.getNumeroViaPrincipal());
    assertEquals("A", farmacia.getLetraViaPrincipal());
    assertNull(farmacia.getLetraViaGeneradora());
    assertNull(farmacia.getComplemento());
    assertNull(farmacia.getHorarioAtencion());
    assertEquals("Carrera 15A # 7 - 30", dto.direccion());
  }

  @Test
  void actualizarGuardaElHorarioSinEspaciosSobrantes() {
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("Farmacia Central", "Calle", "10",
        null, "5", null, "20", null, "  Lun-Sab 8:00-18:00 ", null, null);

    servicio.actualizar(FARMACIA_ID, datos);

    assertEquals("Lun-Sab 8:00-18:00", farmacia.getHorarioAtencion());
  }

  @Test
  void sinCoordenadasElMarcadorSeQuedaDondeEstaba() {
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("Farmacia Central", "Calle", "10",
        null, "5", null, "20", null, null, null, null);

    servicio.actualizar(FARMACIA_ID, datos);

    assertEquals(LATITUD_ORIGINAL, farmacia.getLatitud());
    assertEquals(LONGITUD_ORIGINAL, farmacia.getLongitud());
  }

  @Test
  void conLasDosCoordenadasSeMueveElMarcador() {
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("Farmacia Central", "Calle", "10",
        null, "5", null, "20", null, null, LATITUD_NUEVA, LONGITUD_NUEVA);

    DatosFarmaciaDto dto = servicio.actualizar(FARMACIA_ID, datos);

    assertEquals(LATITUD_NUEVA, farmacia.getLatitud());
    assertEquals(LONGITUD_NUEVA, farmacia.getLongitud());
    assertEquals(LATITUD_NUEVA, dto.latitud());
  }

  @Test
  void unaCoordenadaSolaSeRechazaYNoCambiaNada() {
    GuardarDatosFarmaciaDto soloLatitud = new GuardarDatosFarmaciaDto("Nombre nuevo", "Calle", "10",
        null, "5", null, "20", null, null, LATITUD_NUEVA, null);
    GuardarDatosFarmaciaDto soloLongitud = new GuardarDatosFarmaciaDto("Nombre nuevo", "Calle", "10",
        null, "5", null, "20", null, null, null, LONGITUD_NUEVA);

    assertThrows(DatosInvalidosException.class, () -> servicio.actualizar(FARMACIA_ID, soloLatitud));
    assertThrows(DatosInvalidosException.class, () -> servicio.actualizar(FARMACIA_ID, soloLongitud));

    assertEquals("Farmacia vieja", farmacia.getNombreFarmacia());
    assertEquals(LATITUD_ORIGINAL, farmacia.getLatitud());
    verify(farmaciaRepository, never()).findById(any());
  }

  @Test
  void laFechaDeActualizacionSeRenuevaEnSegundosExactos() {
    LocalDateTime antes = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("Farmacia Central", "Calle", "10",
        null, "5", null, "20", null, null, null, null);

    DatosFarmaciaDto dto = servicio.actualizar(FARMACIA_ID, datos);

    LocalDateTime fecha = farmacia.getFechaUltimaActualizacion();
    assertNotNull(fecha);
    assertEquals(0, fecha.getNano());
    assertFalse(fecha.isBefore(antes));
    assertEquals(fecha, dto.fechaUltimaActualizacion());
  }

  @Test
  void unaFarmaciaPendienteNoPuedeVerNiEditarSusDatos() {
    when(farmaciaRepository.findById(FARMACIA_ID)).thenReturn(Optional.of(farmacia("pendiente")));
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("Farmacia Central", "Calle", "10",
        null, "5", null, "20", null, null, null, null);

    assertThrows(AccesoPanelDenegadoException.class, () -> servicio.obtener(FARMACIA_ID));
    assertThrows(AccesoPanelDenegadoException.class, () -> servicio.actualizar(FARMACIA_ID, datos));
  }

  @Test
  void unaFarmaciaInexistenteSeRechaza() {
    when(farmaciaRepository.findById(FARMACIA_ID)).thenReturn(Optional.empty());

    assertThrows(AccesoPanelDenegadoException.class, () -> servicio.obtener(FARMACIA_ID));
  }

  @Test
  void actualizarAtiendeLasAlertasDeInactividad() {
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("Farmacia Central", "Calle", "10",
        null, "5", null, "20", null, null, null, null);

    servicio.actualizar(FARMACIA_ID, datos);

    verify(actividadFarmacia).atenderAlertas(FARMACIA_ID);
    // La fecha ya la mueve este service: llamar registrarActividad la movería dos veces.
    verify(actividadFarmacia, never()).registrarActividad(any());
  }

  @Test
  void unaActualizacionRechazadaNoAtiendeAlertas() {
    GuardarDatosFarmaciaDto soloLatitud = new GuardarDatosFarmaciaDto("Nombre nuevo", "Calle", "10",
        null, "5", null, "20", null, null, LATITUD_NUEVA, null);

    assertThrows(DatosInvalidosException.class, () -> servicio.actualizar(FARMACIA_ID, soloLatitud));

    verify(actividadFarmacia, never()).atenderAlertas(any());
  }

  @Test
  void unaFarmaciaPendienteNoAtiendeAlertasAlEditar() {
    when(farmaciaRepository.findById(FARMACIA_ID)).thenReturn(Optional.of(farmacia("pendiente")));
    GuardarDatosFarmaciaDto datos = new GuardarDatosFarmaciaDto("Farmacia Central", "Calle", "10",
        null, "5", null, "20", null, null, null, null);

    assertThrows(AccesoPanelDenegadoException.class, () -> servicio.actualizar(FARMACIA_ID, datos));

    verify(actividadFarmacia, never()).atenderAlertas(any());
  }

  private FarmaciasEntity farmacia(String nombreEstado) {
    EstadosFarmaciaEntity estado = new EstadosFarmaciaEntity();
    estado.setNombre(nombreEstado);

    FarmaciasEntity nueva = new FarmaciasEntity();
    nueva.setUsuarioId(FARMACIA_ID);
    nueva.setNombreFarmacia("Farmacia vieja");
    nueva.setTipoViaPrincipal("Calle");
    nueva.setNumeroViaPrincipal("10");
    nueva.setNumeroViaGeneradora("5");
    nueva.setNumeroPredio("20");
    nueva.setLatitud(LATITUD_ORIGINAL);
    nueva.setLongitud(LONGITUD_ORIGINAL);
    nueva.setEstadoAprobacion(estado);
    nueva.setFechaUltimaActualizacion(LocalDateTime.of(2020, 1, 1, 0, 0));
    return nueva;
  }
}