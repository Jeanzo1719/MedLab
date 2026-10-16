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

/**
 * Qué hace: arma el detalle de un medicamento: su información básica y un reporte de
 * disponibilidad por cada farmacia que lo reportó, con su tiempo relativo y su confianza.
 *
 * Cómo entenderlo: es como el empleado que arma la ficha de un producto: busca el producto
 * en el catálogo, pregunta en cada tienda cuándo actualizó su información, consulta las
 * reseñas y entrega todo junto en una sola hoja.
 *
 * Detalles técnicos: obtiene el medicamento de MedicamentoRepository, sus reportes de
 * DisponibilidadRepository y el conteo de calificaciones de todos los reportes con una sola
 * consulta a CalificacionRepository. Calcula el texto relativo con TiempoRelativoService y
 * la confianza con ConfianzaService, usando la hora del servidor como "ahora". Entrega un
 * MedicamentoDetalleDto a MedicamentoDetalleController. Solo lee la base de datos: no
 * guarda ni modifica nada.
 */
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
   * Devuelve el detalle de un medicamento.
   *
   * id: id del medicamento.
   *
   * Si el medicamento no existe, lanza MedicamentoNoEncontradoException. Si existe pero
   * ninguna farmacia lo ha reportado, devuelve el medicamento con la lista de reportes vacía.
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

  /**
   * Cuenta las calificaciones de todos los reportes en una sola consulta y las organiza por
   * id de reporte. Los reportes sin calificaciones no aparecen en el resultado.
   */
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

  /**
   * Convierte un reporte en el dato que se envía al frontend. Si conteo es null, el reporte
   * no tiene calificaciones y su mensaje es "sin calificaciones aún".
   */
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