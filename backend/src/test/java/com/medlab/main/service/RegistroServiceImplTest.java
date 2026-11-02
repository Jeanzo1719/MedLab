package com.medlab.main.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medlab.main.dto.RegistroFarmaciaRequestDto;
import com.medlab.main.dto.RegistroPacienteRequestDto;
import com.medlab.main.dto.RegistroResponseDto;
import com.medlab.main.entity.EstadosFarmaciaEntity;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.entity.PacientesEntity;
import com.medlab.main.entity.RolesEntity;
import com.medlab.main.entity.UsuariosEntity;
import com.medlab.main.exception.IdentificacionDuplicadaException;
import com.medlab.main.repository.EstadosFarmaciaRepository;
import com.medlab.main.repository.FarmaciasRepository;
import com.medlab.main.repository.PacientesRepository;
import com.medlab.main.repository.RolesRepository;
import com.medlab.main.repository.UsuariosRepository;
import com.medlab.main.service.impl.RegistroServiceImpl;

/**
 * Pruebas del registro sin base de datos. Cubren lo que no se ve leyendo el código: que la
 * contraseña se guarde hasheada, que la farmacia nazca pendiente con un UUID, que la cédula
 * duplicada no cree nada y que el segundo nombre vacío se guarde como null.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistroServiceImplTest {

  private static final Integer USUARIO_ID = 42;
  private static final String HASH_ARGON2 = "hash-argon2";
  private static final String CONTRASENA = "Clave#123";
  private static final Pattern PATRON_UUID = Pattern.compile(
      "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

  @Mock
  private UsuariosRepository usuariosRepository;
  @Mock
  private PacientesRepository pacientesRepository;
  @Mock
  private FarmaciasRepository farmaciasRepository;
  @Mock
  private RolesRepository rolesRepository;
  @Mock
  private EstadosFarmaciaRepository estadosFarmaciaRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  private RegistroServiceImpl servicio;

  @BeforeEach
  void preparar() {
    servicio = new RegistroServiceImpl(usuariosRepository, pacientesRepository,
        farmaciasRepository, rolesRepository, estadosFarmaciaRepository, passwordEncoder);

    when(rolesRepository.findByNombre(RegistroService.ROL_PACIENTE))
        .thenReturn(Optional.of(rol(RegistroService.ROL_PACIENTE)));
    when(rolesRepository.findByNombre(RegistroService.ROL_FARMACIA))
        .thenReturn(Optional.of(rol(RegistroService.ROL_FARMACIA)));
    when(estadosFarmaciaRepository.findByNombre(RegistroService.ESTADO_PENDIENTE))
        .thenReturn(Optional.of(estadoPendiente()));
    when(passwordEncoder.encode(CONTRASENA)).thenReturn(HASH_ARGON2);
    when(usuariosRepository.save(any(UsuariosEntity.class))).thenAnswer((invocacion) -> {
      UsuariosEntity usuario = invocacion.getArgument(0);
      usuario.setId(USUARIO_ID);
      return usuario;
    });
  }

  @Test
  void registrarPacienteCreaLaCuentaYElPerfil() {
    RegistroPacienteRequestDto datos = datosPaciente("12345678", "Ana María", "  ");

    RegistroResponseDto respuesta = servicio.registrarPaciente(datos);

    assertEquals(new RegistroResponseDto(USUARIO_ID, "12345678", RegistroService.ROL_PACIENTE,
        RegistroService.ESTADO_ACTIVO), respuesta);

    ArgumentCaptor<UsuariosEntity> usuarioCapturado = ArgumentCaptor.forClass(UsuariosEntity.class);
    verify(usuariosRepository).save(usuarioCapturado.capture());
    UsuariosEntity usuario = usuarioCapturado.getValue();
    assertEquals("12345678", usuario.getIdentificacion());
    assertEquals("ana@correo.com", usuario.getCorreo());
    assertEquals(HASH_ARGON2, usuario.getContrasenaHash());
    assertEquals("3001234567", usuario.getTelefono());
    assertEquals(RegistroService.ROL_PACIENTE, usuario.getRol().getNombre());

    ArgumentCaptor<PacientesEntity> pacienteCapturado = ArgumentCaptor.forClass(PacientesEntity.class);
    verify(pacientesRepository).save(pacienteCapturado.capture());
    PacientesEntity paciente = pacienteCapturado.getValue();
    assertEquals(USUARIO_ID, paciente.getUsuarioId());
    assertEquals("Ana María", paciente.getPrimerNombre());
    assertNull(paciente.getSegundoNombre());
    assertEquals("Pérez Gómez", paciente.getApellidos());
    assertEquals("Sura", paciente.getEpsAfiliada());
    verify(passwordEncoder).encode(CONTRASENA);
  }

  @Test
  void registrarPacienteGuardaLosNombresAdicionalesEnElSegundoNombre() {
    RegistroPacienteRequestDto datos = datosPaciente("12345678", "Ana María",
        "  De los Ángeles Isabel  ");

    servicio.registrarPaciente(datos);

    ArgumentCaptor<PacientesEntity> pacienteCapturado = ArgumentCaptor.forClass(PacientesEntity.class);
    verify(pacientesRepository).save(pacienteCapturado.capture());
    assertEquals("De los Ángeles Isabel", pacienteCapturado.getValue().getSegundoNombre());
  }

  @Test
  void registrarPacienteRechazaUnaCedulaDuplicada() {
    when(usuariosRepository.existsByIdentificacion("12345678")).thenReturn(true);

    assertThrows(IdentificacionDuplicadaException.class,
        () -> servicio.registrarPaciente(datosPaciente("12345678", "Ana María", null)));
    verify(usuariosRepository, never()).save(any(UsuariosEntity.class));
    verify(pacientesRepository, never()).save(any(PacientesEntity.class));
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  void registrarFarmaciaLaDejaPendienteConUuid() {
    RegistroFarmaciaRequestDto datos = datosFarmacia();

    RegistroResponseDto respuesta = servicio.registrarFarmacia(datos);

    ArgumentCaptor<UsuariosEntity> usuarioCapturado = ArgumentCaptor.forClass(UsuariosEntity.class);
    verify(usuariosRepository).save(usuarioCapturado.capture());
    UsuariosEntity usuario = usuarioCapturado.getValue();

    assertEquals(new RegistroResponseDto(USUARIO_ID, usuario.getIdentificacion(),
        RegistroService.ROL_FARMACIA, RegistroService.ESTADO_PENDIENTE), respuesta);
    assertTrue(PATRON_UUID.matcher(usuario.getIdentificacion()).matches(),
        "La farmacia debe identificarse con un UUID");
    assertEquals(RegistroService.ROL_FARMACIA, usuario.getRol().getNombre());
    assertEquals(HASH_ARGON2, usuario.getContrasenaHash());

    ArgumentCaptor<FarmaciasEntity> farmaciaCapturada = ArgumentCaptor.forClass(FarmaciasEntity.class);
    verify(farmaciasRepository).save(farmaciaCapturada.capture());
    FarmaciasEntity farmacia = farmaciaCapturada.getValue();
    assertEquals(USUARIO_ID, farmacia.getUsuarioId());
    assertEquals("Droguería Central", farmacia.getNombreFarmacia());
    assertEquals("Calle", farmacia.getTipoViaPrincipal());
    assertEquals("10", farmacia.getNumeroViaPrincipal());
    assertEquals("A", farmacia.getLetraViaPrincipal());
    assertEquals("5", farmacia.getNumeroViaGeneradora());
    assertEquals("B", farmacia.getLetraViaGeneradora());
    assertEquals("20", farmacia.getNumeroPredio());
    assertNull(farmacia.getComplemento());
    assertEquals("900123456", farmacia.getNit());
    assertEquals("RUT-900123456", farmacia.getRut());
    assertEquals("MAT12345", farmacia.getMatriculaMercantil());
    assertEquals("98765432", farmacia.getIdentificacionDirectorTecnico());
    assertEquals("LIC-2026-001", farmacia.getLicenciaSanitaria());
    assertEquals("Sura", farmacia.getEpsAfiliada());
    assertEquals(new BigDecimal("6.2442"), farmacia.getLatitud());
    assertEquals(new BigDecimal("-75.5812"), farmacia.getLongitud());
    assertEquals(RegistroService.ESTADO_PENDIENTE,
        farmacia.getEstadoAprobacion().getNombre());
  }

  @Test
  void registrarFarmaciaGuardaElComplementoCuandoLlega() {
    RegistroFarmaciaRequestDto base = datosFarmacia();
    RegistroFarmaciaRequestDto datos = new RegistroFarmaciaRequestDto(base.correo(),
        base.contrasena(), base.telefono(), base.epsAfiliada(), base.nombreFarmacia(),
        base.tipoViaPrincipal(), base.numeroViaPrincipal(), base.letraViaPrincipal(),
        base.numeroViaGeneradora(), base.letraViaGeneradora(), base.numeroPredio(),
        "  Local 3  ", base.nit(), base.rut(), base.matriculaMercantil(),
        base.identificacionDirectorTecnico(), base.licenciaSanitaria(), base.latitud(),
        base.longitud());

    servicio.registrarFarmacia(datos);

    ArgumentCaptor<FarmaciasEntity> farmaciaCapturada = ArgumentCaptor.forClass(FarmaciasEntity.class);
    verify(farmaciasRepository).save(farmaciaCapturada.capture());
    assertEquals("Local 3", farmaciaCapturada.getValue().getComplemento());
  }

  private static RegistroPacienteRequestDto datosPaciente(String identificacion,
      String primerNombre, String segundoNombre) {
    return new RegistroPacienteRequestDto(identificacion, " ana@correo.com ", CONTRASENA,
        " 3001234567 ", "Sura", " " + primerNombre + " ", segundoNombre, " Pérez Gómez ");
  }

  private static RegistroFarmaciaRequestDto datosFarmacia() {
    return new RegistroFarmaciaRequestDto(" farmacia@correo.com ", CONTRASENA, "3001234567",
        "Sura", " Droguería Central ", " Calle ", " 10 ", " a ", " 5 ", " b ", " 20 ", "  ",
        " 900123456 ", " RUT-900123456 ", "MAT12345", "98765432", "LIC-2026-001", 6.2442,
        -75.5812);
  }

  private static RolesEntity rol(String nombre) {
    RolesEntity rol = new RolesEntity();
    rol.setNombre(nombre);
    return rol;
  }

  private static EstadosFarmaciaEntity estadoPendiente() {
    EstadosFarmaciaEntity estado = new EstadosFarmaciaEntity();
    estado.setNombre(RegistroService.ESTADO_PENDIENTE);
    return estado;
  }
}
