package com.medlab.main.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.InicioSesionRequestDto;
import com.medlab.main.dto.SesionActualDto;
import com.medlab.main.dto.SesionResponseDto;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.entity.SesionesEntity;
import com.medlab.main.entity.UsuariosEntity;
import com.medlab.main.exception.CredencialesInvalidasException;
import com.medlab.main.exception.FarmaciaNoAprobadaException;
import com.medlab.main.exception.SesionInvalidaException;
import com.medlab.main.repository.FarmaciasRepository;
import com.medlab.main.repository.SesionesRepository;
import com.medlab.main.repository.UsuariosRepository;
import com.medlab.main.service.JwtService;
import com.medlab.main.service.SesionService;

/**
 * Qué hace: recibe la identificación y la contraseña, comprueba que sean correctas, y si lo son
 * crea la sesión de dos semanas con su JWT.
 *
 * Cómo entenderlo: es la portería del edificio. El paciente entra con su cédula; la farmacia,
 * con el código que se le dio al registrarse, y solo si el administrador ya la aprobó. Al salir,
 * la pulsera (el token) se anula y no vuelve a abrir ninguna puerta.
 *
 * Detalles técnicos: la contraseña se compara con Argon2 contra contrasena_hash, nunca se guarda
 * ni se devuelve. Si las credenciales fallan el mensaje es siempre el mismo, para no decir cuál
 * de las dos cosas estaba mal. Al crear la sesión se cierran las anteriores del mismo usuario
 * (una sola sesión activa) y el token se guarda en sesiones.token, que es UNIQUE.
 */
@Service
public class SesionServiceImpl implements SesionService {

  private final UsuariosRepository usuariosRepository;
  private final FarmaciasRepository farmaciasRepository;
  private final SesionesRepository sesionesRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final long duracionDias;

  public SesionServiceImpl(UsuariosRepository usuariosRepository,
      FarmaciasRepository farmaciasRepository, SesionesRepository sesionesRepository,
      JwtService jwtService, PasswordEncoder passwordEncoder,
      @Value("${medlab.jwt.duracion-dias:14}") long duracionDias) {
    this.usuariosRepository = usuariosRepository;
    this.farmaciasRepository = farmaciasRepository;
    this.sesionesRepository = sesionesRepository;
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.duracionDias = duracionDias;
  }

  @Override
  @Transactional
  public SesionResponseDto iniciarSesion(InicioSesionRequestDto datos) {
    String identificacion = datos.identificacion().trim();
    UsuariosEntity usuario = usuariosRepository.findByIdentificacion(identificacion)
        .orElseThrow(CredencialesInvalidasException::new);

    if (!passwordEncoder.matches(datos.contrasena(), usuario.getContrasenaHash())) {
      throw new CredencialesInvalidasException();
    }

    validarFarmaciaAprobada(usuario);

    // Sin nanosegundos: DATETIME guarda segundos y así el token y la fila vencen a la vez.
    LocalDateTime inicio = LocalDateTime.now().withNano(0);
    LocalDateTime expiracion = inicio.plusDays(duracionDias);

    // Una sola sesión por usuario: la anterior se cierra y la nueva ocupa su lugar.
    sesionesRepository.cerrarActivasDeUsuario(usuario.getId(), inicio);

    ZoneId zona = ZoneId.systemDefault();
    String token = jwtService.generarToken(usuario.getId(), usuario.getRol().getId(),
        usuario.getRol().getNombre(), inicio.atZone(zona).toInstant(),
        expiracion.atZone(zona).toInstant());

    SesionesEntity sesion = new SesionesEntity();
    sesion.setUsuario(usuario);
    sesion.setToken(token);
    sesion.setFechaInicio(inicio);
    sesion.setFechaExpiracion(expiracion);
    sesion.setActiva(true);
    sesionesRepository.save(sesion);

    return new SesionResponseDto(token, usuario.getRol().getNombre(),
        usuario.getIdentificacion(), expiracion);
  }

  @Override
  @Transactional(readOnly = true)
  public SesionActualDto obtenerSesionActual(String token) {
    SesionesEntity sesion = sesionesRepository.findByToken(token)
        .orElseThrow(SesionInvalidaException::new);
    exigirVigente(sesion);

    UsuariosEntity usuario = sesion.getUsuario();
    return new SesionActualDto(usuario.getId(), usuario.getIdentificacion(),
        usuario.getRol().getNombre(), sesion.getFechaExpiracion());
  }

  @Override
  @Transactional
  public void cerrarSesion(String token) {
    SesionesEntity sesion = sesionesRepository.findByToken(token).orElse(null);
    if (sesion == null || !sesion.isActiva()) {
      // Cerrar dos veces no es un error: la sesión ya estaba cerrada.
      return;
    }
    sesion.setActiva(false);
    sesion.setFechaCierre(LocalDateTime.now().withNano(0));
    sesionesRepository.save(sesion);
  }

  /** Una farmacia solo entra si su estado en estados_farmacia es exactamente 'aprobada'. */
  private void validarFarmaciaAprobada(UsuariosEntity usuario) {
    if (!ROL_FARMACIA.equals(usuario.getRol().getNombre())) {
      return;
    }

    FarmaciasEntity farmacia = farmaciasRepository.findById(usuario.getId())
        .orElseThrow(() -> new FarmaciaNoAprobadaException(null));
    String estado = farmacia.getEstadoAprobacion().getNombre();
    if (!ESTADO_APROBADA.equals(estado)) {
      throw new FarmaciaNoAprobadaException(estado);
    }
  }

  private void exigirVigente(SesionesEntity sesion) {
    if (!sesion.isActiva() || !sesion.getFechaExpiracion().isAfter(LocalDateTime.now())) {
      throw new SesionInvalidaException();
    }
  }
}
