package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.DisponibilidadSospechosaDto;
import com.medlab.main.repository.HistorialAdminRepository;
import com.medlab.main.repository.HistorialAdminRepository.DisponibilidadSospechosa;

import lombok.RequiredArgsConstructor;

/**
 * Detecta publicaciones con 15 o más calificaciones "No hay" entre las últimas 20 (HU-9). Mirar
 * solo las últimas 20 hace que una publicación que ya se corrigió deje de aparecer sola.
 */
@Service
@RequiredArgsConstructor
public class CalificacionesSospechosasService {

  public static final int VENTANA = 20;
  public static final int UMBRAL_NO_HAY = 15;

  /** Escrito igual que en tipos_calificacion. */
  private static final String TIPO_NO_HAY = "No hay";
  private static final int MAXIMO_LISTA = 100;
  /** 0 en la consulta significa "todas las publicaciones". */
  private static final int TODAS = 0;

  private final HistorialAdminRepository historialRepository;
  private final NotificadorAlertas notificador;

  /** Todas las publicaciones sospechosas, para el panel (no depende de estar conectado). */
  @Transactional(readOnly = true)
  public List<DisponibilidadSospechosaDto> listar() {
    return buscar(TODAS).stream().map(this::aDto).toList();
  }

  /**
   * Avisa al administrador si esta publicación pasó el umbral. Debe llamarlo quien guarde una
   * calificación, después de guardarla (y mejor tras el commit, para que el panel ya vea el dato).
   */
  @Transactional(readOnly = true)
  public void revisarDisponibilidad(Integer disponibilidadId) {
    buscar(disponibilidadId).stream().findFirst().map(this::aDto)
        .ifPresent(notificador::calificacionesSospechosas);
  }

  private List<DisponibilidadSospechosa> buscar(int disponibilidadId) {
    return historialRepository.buscarSospechosas(disponibilidadId, VENTANA, UMBRAL_NO_HAY,
        TIPO_NO_HAY, MAXIMO_LISTA);
  }

  private DisponibilidadSospechosaDto aDto(DisponibilidadSospechosa d) {
    return new DisponibilidadSospechosaDto(d.getId(), d.getFarmaciaId(), d.getFarmacia(),
        d.getMedicamento(), d.getPresentacion(), valor(d.getCalificacionesNoHay()),
        valor(d.getCalificacionesRevisadas()));
  }

  private long valor(Long conteo) {
    return conteo == null ? 0 : conteo;
  }
}