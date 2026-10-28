package com.medlab.main.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.AuditoriaEstadoDto;
import com.medlab.main.dto.CalificacionAdminDto;
import com.medlab.main.dto.DisponibilidadAdminDto;
import com.medlab.main.dto.FarmaciaConAlertasDto;
import com.medlab.main.dto.FarmaciaHistorialDto;
import com.medlab.main.dto.UsuarioHistorialDto;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.exception.RecursoNoEncontradoException;
import com.medlab.main.repository.AuditoriaEstadoFarmaciaRepository;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.FarmaciaRepository;
import com.medlab.main.repository.HistorialAdminRepository;

import lombok.RequiredArgsConstructor;

/**
 * Consultas del panel administrador (HU-9). Solo lee: lo que modifica algo vive en otros
 * services para que quede claro qué puede cambiar un administrador.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GestionAdminService {

  private static final int TAMANO_MAXIMO_PAGINA = 50;
  private static final Set<String> ROLES = Set.of("paciente", "farmacia", "administrador");
  private static final Set<String> ESTADOS_FARMACIA = Set.of("pendiente", "aprobada", "suspendida");

  private final HistorialAdminRepository historialRepository;
  private final AuditoriaEstadoFarmaciaRepository auditoriaRepository;
  private final FarmaciaRepository farmaciaRepository;
  private final DisponibilidadRepository disponibilidadRepository;

  public Page<UsuarioHistorialDto> usuarios(String rol, int pagina, int tamano) {
    String filtro = filtroValido(rol, ROLES, "Rol");
    return historialRepository.listarUsuarios(filtro, paginacion(pagina, tamano))
        .map(u -> new UsuarioHistorialDto(u.getId(), u.getIdentificacion(), u.getRol(),
            u.getNombre(), u.getFechaRegistro()));
  }

  public Page<FarmaciaHistorialDto> farmacias(String estado, int pagina, int tamano) {
    String filtro = filtroValido(estado, ESTADOS_FARMACIA, "Estado");
    return historialRepository.listarFarmacias(filtro, paginacion(pagina, tamano))
        .map(f -> new FarmaciaHistorialDto(f.getId(), f.getNombre(), f.getEstado(),
            f.getFechaUltimaActualizacion(), valor(f.getAlertasPendientes())));
  }

  public Page<FarmaciaConAlertasDto> farmaciasConAlertas(int pagina, int tamano) {
    return historialRepository.listarFarmaciasConAlertas(paginacion(pagina, tamano))
        .map(f -> new FarmaciaConAlertasDto(f.getId(), f.getNombre(), f.getEstado(),
            valor(f.getTotalAlertas()), valor(f.getAlertasPendientes()), f.getUltimaAlerta()));
  }

  public Page<AuditoriaEstadoDto> auditoria(Integer farmaciaId, int pagina, int tamano) {
    exigirFarmacia(farmaciaId);
    return auditoriaRepository
        .findByFarmaciaUsuarioIdOrderByFechaDescIdDesc(farmaciaId, paginacion(pagina, tamano))
        .map(a -> new AuditoriaEstadoDto(a.getId(), a.getEstadoAnterior().getNombre(),
            a.getEstadoNuevo().getNombre(), a.getUsuarioAdminId(), a.getUsuarioAdminId() == null,
            a.getFecha()));
  }

  public Page<DisponibilidadAdminDto> disponibilidades(Integer farmaciaId, int pagina, int tamano) {
    exigirFarmacia(farmaciaId);
    return historialRepository.listarDisponibilidades(farmaciaId, paginacion(pagina, tamano))
        .map(d -> new DisponibilidadAdminDto(d.getId(), d.getMedicamento(), d.getPresentacion(),
            d.getEstado(), d.getCantidad(), valor(d.getTotalCalificaciones()),
            d.getFechaHoraReporte()));
  }

  public Page<CalificacionAdminDto> calificaciones(Integer disponibilidadId, int pagina, int tamano) {
    if (!disponibilidadRepository.existsById(disponibilidadId)) {
      throw new RecursoNoEncontradoException("La disponibilidad " + disponibilidadId + " no existe");
    }
    return historialRepository.listarCalificaciones(disponibilidadId, paginacion(pagina, tamano))
        .map(c -> new CalificacionAdminDto(c.getId(), c.getUsuarioId(), c.getIdentificacion(),
            c.getTipo(), c.getFechaHora()));
  }

  private void exigirFarmacia(Integer farmaciaId) {
    if (!farmaciaRepository.existsById(farmaciaId)) {
      throw new RecursoNoEncontradoException("La farmacia " + farmaciaId + " no existe");
    }
  }

  /** Vacío significa "sin filtro"; un valor desconocido se rechaza en vez de devolver una lista vacía. */
  private String filtroValido(String valor, Set<String> permitidos, String nombre) {
    if (valor == null || valor.isBlank()) {
      return "";
    }
    String limpio = valor.trim();
    if (!permitidos.contains(limpio)) {
      throw new DatosInvalidosException(nombre + " no válido: " + valor);
    }
    return limpio;
  }

  private Pageable paginacion(int pagina, int tamano) {
    int tamanoSeguro = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO_PAGINA);
    return PageRequest.of(Math.max(pagina, 0), tamanoSeguro);
  }

  /** Un conteo nulo (sin filas que sumar) cuenta como cero. */
  private long valor(Long conteo) {
    return conteo == null ? 0 : conteo;
  }
}