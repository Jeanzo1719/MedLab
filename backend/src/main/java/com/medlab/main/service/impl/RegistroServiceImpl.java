package com.medlab.main.service.impl;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.medlab.main.service.RegistroService;

import lombok.RequiredArgsConstructor;

/**
 * Qué hace: crea la cuenta de un paciente/cuidador o de una farmacia y guarda su perfil en la
 * tabla que le corresponde.
 *
 * Cómo entenderlo: es como la ventanilla de registro. Pide una cédula única al paciente; a la
 * farmacia le entrega un UUID porque no tiene cédula, y deja su solicitud en la fila de
 * "pendiente de aprobación". La contraseña se guarda siempre cifrada con Argon2, nunca en texto
 * plano.
 *
 * Detalles técnicos: usa solo las interfaces propias de repositorio y el PasswordEncoder de
 * Spring Security. La cuenta y su perfil se guardan en una sola transacción: si falla el perfil,
 * la cuenta tampoco queda creada. Correo y teléfono pueden repetirse; la cédula no.
 */
@Service
@RequiredArgsConstructor
public class RegistroServiceImpl implements RegistroService {

  private final UsuariosRepository usuariosRepository;
  private final PacientesRepository pacientesRepository;
  private final FarmaciasRepository farmaciasRepository;
  private final RolesRepository rolesRepository;
  private final EstadosFarmaciaRepository estadosFarmaciaRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public RegistroResponseDto registrarPaciente(RegistroPacienteRequestDto datos) {
    UsuariosEntity usuario = crearCuenta(datos.identificacion().trim(), datos.correo(),
        datos.contrasena(), datos.telefono(), ROL_PACIENTE);

    PacientesEntity paciente = new PacientesEntity();
    paciente.setUsuarioId(usuario.getId());
    paciente.setPrimerNombre(datos.primerNombre().trim());
    // Si la persona tiene tres o más nombres, todos los adicionales caben en este campo.
    paciente.setSegundoNombre(opcional(datos.segundoNombre()));
    paciente.setApellidos(datos.apellidos().trim());
    paciente.setEpsAfiliada(datos.epsAfiliada().trim());
    pacientesRepository.save(paciente);

    return new RegistroResponseDto(usuario.getId(), ROL_PACIENTE, ESTADO_ACTIVO);
  }

  @Override
  @Transactional
  public RegistroResponseDto registrarFarmacia(RegistroFarmaciaRequestDto datos) {
    UsuariosEntity usuario = crearCuenta(UUID.randomUUID().toString(), datos.correo(),
        datos.contrasena(), datos.telefono(), ROL_FARMACIA);

    FarmaciasEntity farmacia = new FarmaciasEntity();
    farmacia.setUsuarioId(usuario.getId());
    farmacia.setNombreFarmacia(datos.nombreFarmacia().trim());
    farmacia.setTipoViaPrincipal(datos.tipoViaPrincipal().trim());
    farmacia.setNumeroViaPrincipal(datos.numeroViaPrincipal().trim());
    farmacia.setLetraViaPrincipal(letra(datos.letraViaPrincipal()));
    farmacia.setNumeroViaGeneradora(datos.numeroViaGeneradora().trim());
    farmacia.setLetraViaGeneradora(letra(datos.letraViaGeneradora()));
    farmacia.setNumeroPredio(datos.numeroPredio().trim());
    farmacia.setComplemento(opcional(datos.complemento()));
    farmacia.setNit(datos.nit().trim());
    farmacia.setRut(datos.rut().trim());
    farmacia.setMatriculaMercantil(datos.matriculaMercantil().trim());
    farmacia.setIdentificacionDirectorTecnico(datos.identificacionDirectorTecnico().trim());
    farmacia.setLicenciaSanitaria(datos.licenciaSanitaria().trim());
    farmacia.setEpsAfiliada(datos.epsAfiliada().trim());
    farmacia.setLatitud(BigDecimal.valueOf(datos.latitud()));
    farmacia.setLongitud(BigDecimal.valueOf(datos.longitud()));
    farmacia.setEstadoAprobacion(buscarEstado(ESTADO_PENDIENTE));
    farmaciasRepository.save(farmacia);

    return new RegistroResponseDto(usuario.getId(), ROL_FARMACIA, ESTADO_PENDIENTE);
  }

  /**
   * Crea la fila de usuarios que comparten pacientes y farmacias. La identificación ya viene
   * decidida: la cédula del paciente o el UUID de la farmacia.
   */
  private UsuariosEntity crearCuenta(String identificacion, String correo, String contrasena,
      String telefono, String nombreRol) {
    if (usuariosRepository.existsByIdentificacion(identificacion)) {
      throw new IdentificacionDuplicadaException(identificacion);
    }

    UsuariosEntity usuario = new UsuariosEntity();
    usuario.setIdentificacion(identificacion);
    usuario.setCorreo(correo.trim());
    // Argon2: el texto plano no toca la base de datos.
    usuario.setContrasenaHash(passwordEncoder.encode(contrasena));
    usuario.setTelefono(telefono.trim());
    usuario.setRol(buscarRol(nombreRol));

    try {
      return usuariosRepository.save(usuario);
    } catch (DataIntegrityViolationException ex) {
      // Dos registros simultáneos con la misma cédula: la base gana y se responde igual.
      throw new IdentificacionDuplicadaException(identificacion);
    }
  }

  private RolesEntity buscarRol(String nombreRol) {
    return rolesRepository.findByNombre(nombreRol)
        .orElseThrow(() -> new IllegalStateException("No existe el rol " + nombreRol));
  }

  private EstadosFarmaciaEntity buscarEstado(String nombreEstado) {
    return estadosFarmaciaRepository.findByNombre(nombreEstado)
        .orElseThrow(() -> new IllegalStateException(
            "No existe el estado de farmacia " + nombreEstado));
  }

  /** Mayúscula siempre, igual que en el panel de la farmacia. */
  private String letra(String valor) {
    String limpio = opcional(valor);
    return limpio == null ? null : limpio.toUpperCase(Locale.ROOT);
  }

  /** Vacío y solo espacios se guardan como null, no como texto en blanco. */
  private String opcional(String valor) {
    if (valor == null || valor.isBlank()) {
      return null;
    }
    return valor.trim();
  }
}
