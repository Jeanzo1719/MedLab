package com.medlab.main.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.ConfianzaDto;
import com.medlab.main.dto.DisponibilidadReporteDto;
import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.entity.Disponibilidad;
import com.medlab.main.entity.Medicamento;
import com.medlab.main.exception.MedicamentoNoEncontradoException;
import com.medlab.main.repository.CalificacionRepository;
import com.medlab.main.repository.CalificacionRepository.ConteoCalificaciones;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.MedicamentoRepository;

import lombok.RequiredArgsConstructor;

/** Arma el detalle de un medicamento: su información básica y un reporte por farmacia. */
@Service
@RequiredArgsConstructor
public class MedicamentoDetalleService {

  private static final String SIN_CALIFICACIONES = "sin calificaciones aún";

  private final MedicamentoRepository medicamentoRepository;
  private final DisponibilidadRepository disponibilidadRepository;
  private final CalificacionRepository calificacionRepository;
  private final ConfianzaService confianzaService;
  private final TiempoRelativoService tiempoRelativoService;

  /**
   * Busca el medicamento y sus reportes de disponibilidad. Las calificaciones de todas las
   * farmacias se cuentan en una sola consulta. Si el medicamento no existe, lanza
   * MedicamentoNoEncontradoException.
   */
  @Transactional(readOnly = true)
  public MedicamentoDetalleDto obtener(Integer id) {
    Medicamento medicamento = medicamentoRepository.findDetalleById(id)
        .orElseThrow(() -> new MedicamentoNoEncontradoException(id));

    List<Disponibilidad> disponibilidades = disponibilidadRepository
        .findByMedicamentoIdConFarmacia(id);
    Map<Integer, ConteoCalificaciones> conteos = contarCalificaciones(disponibilidades);
    LocalDateTime ahora = LocalDateTime.now();

    List<DisponibilidadReporteDto> reportes = disponibilidades.stream()
        .map(d -> aReporteDto(d, conteos.get(d.getId()), ahora)).toList();

    return new MedicamentoDetalleDto(medicamento.getId(), medicamento.getNombreComercial(),
        medicamento.getPrincipioActivo(), medicamento.getCategoria().getNombre(),
        medicamento.getPresentacion(), medicamento.getFormaFarmaceutica().getNombre(), reportes);
  }

  private Map<Integer, ConteoCalificaciones> contarCalificaciones(
      List<Disponibilidad> disponibilidades) {
    if (disponibilidades.isEmpty()) {
      return Map.of();
    }
    List<Integer> ids = disponibilidades.stream().map(Disponibilidad::getId).toList();
    return calificacionRepository.contarPorDisponibilidad(ids, ConfianzaService.TIPO_FAVORABLE)
        .stream()
        .collect(Collectors.toMap(ConteoCalificaciones::getDisponibilidadId, Function.identity()));
  }

  private DisponibilidadReporteDto aReporteDto(Disponibilidad disponibilidad,
      ConteoCalificaciones conteo, LocalDateTime ahora) {
    LocalDateTime fecha = disponibilidad.getFechaHoraReporte();

    Optional<ConfianzaDto> confianza = conteo == null ? Optional.empty()
        : confianzaService.calcular(conteo.getFavorables(), conteo.getTotal(), fecha, ahora);

    String mensaje = confianza
        .map(c -> "Confianza " + c.nivel().name().toLowerCase(Locale.ROOT))
        .orElse(SIN_CALIFICACIONES);

    return new DisponibilidadReporteDto(disponibilidad.getId(),
        disponibilidad.getFarmacia().getNombreFarmacia(), fecha,
        tiempoRelativoService.desde(fecha, ahora), confianza.orElse(null), mensaje);
  }
}