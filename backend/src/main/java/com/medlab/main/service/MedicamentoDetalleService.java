package com.medlab.main.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.DisponibilidadReporteDto;
import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.dto.TendenciaDisponibilidad;
import com.medlab.main.entity.Disponibilidad;
import com.medlab.main.entity.Medicamento;
import com.medlab.main.exception.MedicamentoNoEncontradoException;
import com.medlab.main.repository.CalificacionRepository;
import com.medlab.main.repository.CalificacionRepository.CalificacionReciente;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.MedicamentoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Qué hace: arma el detalle de un medicamento: su información básica y un reporte de
 * disponibilidad por cada farmacia que lo reportó, con su tiempo relativo y lo que dicen las
 * calificaciones recientes.
 *
 * Cómo entenderlo: es como el empleado que arma la ficha de un producto: busca el producto
 * en el catálogo, pregunta en cada tienda cuándo actualizó su información, consulta las
 * últimas reseñas y entrega todo junto en una sola hoja.
 *
 * Detalles técnicos: obtiene el medicamento de MedicamentoRepository, sus reportes de
 * DisponibilidadRepository y las calificaciones recientes de todos los reportes con una sola
 * consulta a CalificacionRepository. Calcula el texto relativo con TiempoRelativoService y la
 * tendencia de disponibilidad con MensajeDisponibilidadService, usando la hora del servidor
 * como "ahora". Entrega un MedicamentoDetalleDto a MedicamentoDetalleController. Solo lee la
 * base de datos: no guarda ni modifica nada.
 */
@Service
@RequiredArgsConstructor
public class MedicamentoDetalleService {

  private final MedicamentoRepository medicamentoRepository;
  private final DisponibilidadRepository disponibilidadRepository;
  private final CalificacionRepository calificacionRepository;
  private final MensajeDisponibilidadService mensajeDisponibilidadService;
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
    Map<Integer, List<String>> tiposRecientes = buscarTiposRecientes(disponibilidades);
    LocalDateTime ahora = LocalDateTime.now();

    List<DisponibilidadReporteDto> reportes = disponibilidades.stream()
        .map(d -> aReporteDto(d, tiposRecientes.getOrDefault(d.getId(), List.of()), ahora))
        .toList();

    return new MedicamentoDetalleDto(medicamento.getId(), medicamento.getNombreComercial(),
        medicamento.getPrincipioActivo(), medicamento.getCategoria().getNombre(),
        medicamento.getPresentacion(), medicamento.getFormaFarmaceutica().getNombre(), reportes);
  }

  /**
   * Trae las calificaciones recientes de todos los reportes en una sola consulta y las
   * organiza por id de reporte, como lista de nombres de tipo. Los reportes sin
   * calificaciones no aparecen en el resultado.
   */
  private Map<Integer, List<String>> buscarTiposRecientes(List<Disponibilidad> disponibilidades) {
    if (disponibilidades.isEmpty()) {
      return Map.of();
    }
    List<Integer> ids = disponibilidades.stream().map(Disponibilidad::getId).toList();
    return calificacionRepository
        .buscarRecientesPorDisponibilidad(ids, MensajeDisponibilidadService.CALIFICACIONES_RECIENTES)
        .stream()
        .collect(Collectors.groupingBy(CalificacionReciente::getDisponibilidadId,
            Collectors.mapping(CalificacionReciente::getTipo, Collectors.toList())));
  }

  /**
   * Convierte un reporte en el dato que se envía al frontend. Si tiposRecientes está vacía,
   * el reporte no tiene calificaciones y su mensaje es "sin calificaciones aún".
   */
  private DisponibilidadReporteDto aReporteDto(Disponibilidad disponibilidad,
      List<String> tiposRecientes, LocalDateTime ahora) {
    LocalDateTime fecha = disponibilidad.getFechaHoraReporte();
    TendenciaDisponibilidad tendencia = mensajeDisponibilidadService.calcular(tiposRecientes);

    return new DisponibilidadReporteDto(disponibilidad.getId(),
        disponibilidad.getFarmacia().getNombreFarmacia(), fecha,
        tiempoRelativoService.desde(fecha, ahora), tendencia,
        mensajeDisponibilidadService.mensaje(tendencia, fecha, ahora));
  }
}