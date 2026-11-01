package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medlab.main.dto.InicioSesionRequestDto;
import com.medlab.main.dto.SesionActualDto;
import com.medlab.main.dto.SesionResponseDto;
import com.medlab.main.entity.EstadosFarmaciaEntity;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.entity.RolesEntity;
import com.medlab.main.entity.SesionesEntity;
import com.medlab.main.entity.UsuariosEntity;
import com.medlab.main.exception.CredencialesInvalidasException;
import com.medlab.main.exception.FarmaciaNoAprobadaException;
import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.repository.FarmaciasRepository;
import com.medlab.main.repository.SesionesRepository;
import com.medlab.main.repository.UsuariosRepository;
import com.medlab.main.service.impl.SesionServiceImpl;

/**
 * Pruebas del inicio y cierre de sesión sin base de datos. Cubren lo que no se ve leyendo el
 * código: el mensaje genérico, las dos semanas de vigencia, el cierre de la sesión anterior y
 * los estados de una farmacia.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SesionServiceImplTest {

  private static final Integer USUARIO_ID = 42;
  private static final String IDENTIFICACION = "12345678";
  private static final String CONTRASENA = "Clave#123";
  private static final String HASH = "hash-argon2";
  private static final String TOKEN = "token-jwt";
  private static final long DIAS = 14;
  private static final String ROL_PACIENTE = "paciente";
  private static final String ROL_FARMACIA = "farmacia";
  private static final String ROL_APROBADA = "aprobada";

  @Mock
  private UsuariosRepository usuariosRepository;
  @Mock
  private FarmaciasRepository farmaciasRepository;
  @Mock
  private SesionesRepository sesionesRepository;
  @Mock
  private JwtService jwtService;
  @Mock
  private PasswordEncoder passwordEncoder;

  private SesionServiceImpl servicio;

  @BeforeEach
  void preparar() {
    servicio = new SesionServiceImpl(usuariosRepository, farmaciasRepository, sesionesRepository,
        jwtService, passwordEncoder, DIAS);

    when(jwtService.generarToken(anyInt(), any(), anyString(), any(), any())).thenReturn(TOKEN);
    when(usuariosRepository.save(any(UsuariosEntity.class))).thenAnswer((invocacion) -> {
      UsuariosEntity usuario = invocacion.getArgument(0);
      usuario.setId(USUARIO_ID);
      return usuario;
    });
    when(sesionesRepository.save(any(SesionesEntity.class)))
        .thenAnswer((invocacion) -> invocacion.getArgument(0));
  }

  @Test
  void unPacienteConCredencialesCorrectasEntraYSesionDuraDosSemanas() {
    when(usuariosRepository.findByIdentificacion(IDENTIFICACION))
        .thenReturn(Optional.of(usuario(ROL_PACIENTE)));
    when(passwordEncoder.matches(CONTRASENA, HASH)).thenReturn(true);

    SesionResponseDto respuesta = servicio.iniciarSesion(datos());

    assertEquals(TOKEN, respuesta.token());
    assertEquals(ROL_PACIENTE, respuesta.rol());
    assertEquals(IDENTIFICACION, respuesta.identificacion());
    verify(passwordEncoder).matches(CONTRASENA, HASH);

    ArgumentCaptor<SesionesEntity> sesionCapturada = ArgumentCaptor.forClass(SesionesEntity.class);
    verify(sesionesRepository).save(sesionCapturada.capture());
    SesionesEntity sesion = sesionCapturada.getValue();
    assertTrue(sesion.isActiva());
    assertEquals(TOKEN, sesion.getToken());
    assertEquals(USUARIO_ID, sesion.getUsuario().getId());
    assertEquals(sesion.getFechaInicio().plusDays(DIAS), sesion.getFechaExpiracion());
    assertEquals(sesion.getFechaExpiracion(), respuesta.expiraEn());

    // La sesión anterior del mismo usuario se cierra antes de crear la nueva.
    verify(sesionesRepository).cerrarActivasDeUsuario(USUARIO_ID, sesion.getFechaInicio());
  }

  @Test
  void unaContrasenaIncorrectaNoCreaSesion() {
    when(usuariosRepository.findByIdentificacion(IDENTIFICACION))
        .thenReturn(Optional.of(usuario(ROL_PACIENTE)));
    when(passwordEncoder.matches(CONTRASENA, HASH)).thenReturn(false);

    assertThrows(CredencialesInvalidasException.class, () -> servicio.iniciarSesion(datos()));
    verify(sesionesRepository, never()).save(any(SesionesEntity.class));
    verify(jwtService, never()).generarToken(anyInt(), any(), anyString(), any(), any());
  }

  @Test
  void unaIdentificacionQueNoExisteRespondeElMismoError() {
    when(usuariosRepository.findByIdentificacion(IDENTIFICACION)).thenReturn(Optional.empty());

    assertThrows(CredencialesInvalidasException.class, () -> servicio.iniciarSesion(datos()));
    verify(sesionesRepository, never()).save(any(SesionesEntity.class));
  }

  @Test
  void unaFarmaciaPendienteNoPuedeEntrar() {
    when(usuariosRepository.findByIdentificacion(IDENTIFICACION))
        .thenReturn(Optional.of(usuario(ROL_FARMACIA)));
    when(passwordEncoder.matches(CONTRASENA, HASH)).thenReturn(true);
    when(farmaciasRepository.findById(USUARIO_ID))
        .thenReturn(Optional.of(farmacia("pendiente")));

    FarmaciaNoAprobadaException error = assertThrows(FarmaciaNoAprobadaException.class,
        () -> servicio.iniciarSesion(datos()));

    assertTrue(error.getMessage().contains("espera de aprobación"));
    verify(sesionesRepository, never()).save(any(SesionesEntity.class));
  }

  @Test
  void unaFarmaciaSuspendidaTieneSuPropioMensaje() {
    when(usuariosRepository.findByIdentificacion(IDENTIFICACION))
        .thenReturn(Optional.of(usuario(ROL_FARMACIA)));
    when(passwordEncoder.matches(CONTRASENA, HASH)).thenReturn(true);
    when(farmaciasRepository.findById(USUARIO_ID))
        .thenReturn(Optional.of(farmacia("suspendida")));

    FarmaciaNoAprobadaException error = assertThrows(FarmaciaNoAprobadaException.class,
        () -> servicio.iniciarSesion(datos()));

    assertTrue(error.getMessage().contains("suspendida"));
  }

  @Test
  void unaFarmaciaAprobadaSiPuedeEntrar() {
    when(usuariosRepository.findByIdentificacion(IDENTIFICACION))
        .thenReturn(Optional.of(usuario(ROL_FARMACIA)));
    when(passwordEncoder.matches(CONTRASENA, HASH)).thenReturn(true);
    when(farmaciasRepository.findById(USUARIO_ID))
        .thenReturn(Optional.of(farmacia(ROL_APROBADA)));

    SesionResponseDto respuesta = servicio.iniciarSesion(datos());

    assertEquals(ROL_FARMACIA, respuesta.rol());
    verify(sesionesRepository).save(any(SesionesEntity.class));
  }

  @Test
  void cerrarSesionLaDesactivaYLePoneFechaDeCierre() {
    SesionesEntity sesion = sesionActiva(LocalDateTime.now().plusDays(3));
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(sesion));

    servicio.cerrarSesion(TOKEN);

    assertFalse(sesion.isActiva());
    assertNotNull(sesion.getFechaCierre());
    verify(sesionesRepository).save(sesion);
  }

  @Test
  void cerrarUnaSesionYaCerradaNoHaceNada() {
    SesionesEntity sesion = sesionActiva(LocalDateTime.now().plusDays(3));
    sesion.setActiva(false);
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(sesion));

    servicio.cerrarSesion(TOKEN);

    verify(sesionesRepository, never()).save(any(SesionesEntity.class));
  }

  @Test
  void laSesionActualSeRechazaSiEstaCerradaOVencida() {
    SesionesEntity cerrada = sesionActiva(LocalDateTime.now().plusDays(3));
    cerrada.setActiva(false);
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(cerrada));

    assertThrows(SesionInvalidaException.class, () -> servicio.obtenerSesionActual(TOKEN));

    SesionesEntity vencida = sesionActiva(LocalDateTime.now().minusMinutes(1));
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(vencida));

    assertThrows(SesionInvalidaException.class, () -> servicio.obtenerSesionActual(TOKEN));
  }

  @Test
  void laSesionActualDevuelveElRolYLaIdentificacion() {
    SesionesEntity sesion = sesionActiva(LocalDateTime.now().plusDays(3));
    when(sesionesRepository.findByToken(TOKEN)).thenReturn(Optional.of(sesion));

    SesionActualDto actual = servicio.obtenerSesionActual(TOKEN);

    assertEquals(USUARIO_ID, actual.id());
    assertEquals(IDENTIFICACION, actual.identificacion());
    assertEquals(ROL_PACIENTE, actual.rol());
  }

  private static InicioSesionRequestDto datos() {
    return new InicioSesionRequestDto(IDENTIFICACION, CONTRASENA);
  }

  private static UsuariosEntity usuario(String nombreRol) {
    RolesEntity rol = new RolesEntity();
    rol.setId(ROL_FARMACIA.equals(nombreRol) ? 3 : 1);
    rol.setNombre(nombreRol);

    UsuariosEntity usuario = new UsuariosEntity();
    usuario.setId(USUARIO_ID);
    usuario.setIdentificacion(IDENTIFICACION);
    usuario.setContrasenaHash(HASH);
    usuario.setRol(rol);
    return usuario;
  }

  private static FarmaciasEntity farmacia(String estado) {
    EstadosFarmaciaEntity estadoEntity = new EstadosFarmaciaEntity();
    estadoEntity.setNombre(estado);

    FarmaciasEntity farmacia = new FarmaciasEntity();
    farmacia.setUsuarioId(USUARIO_ID);
    farmacia.setEstadoAprobacion(estadoEntity);
    return farmacia;
  }

  private static SesionesEntity sesionActiva(LocalDateTime expiracion) {
    SesionesEntity sesion = new SesionesEntity();
    sesion.setId(99);
    sesion.setUsuario(usuario(ROL_PACIENTE));
    sesion.setToken(TOKEN);
    sesion.setFechaInicio(LocalDateTime.now().minusDays(1));
    sesion.setFechaExpiracion(expiracion);
    sesion.setActiva(true);
    return sesion;
  }
}
