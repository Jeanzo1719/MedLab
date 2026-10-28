package com.medlab.main.service.impl;

import com.medlab.main.dto.DisponibilidadReporteDto;
import com.medlab.main.dto.MedicamentoDetalleDto;
import com.medlab.main.dto.TendenciaDisponibilidad;
import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.exception.MedicamentoNotFoundException;
import com.medlab.main.repository.CalificacionesRepository;
import com.medlab.main.repository.CalificacionesRepository.CalificacionReciente;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.MedicamentosRepository;
import com.medlab.main.service.MensajeDisponibilidadService;
import com.medlab.main.service.MedicamentoDetalleService;
import com.medlab.main.service.TiempoRelativoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Qué hace: arma el detalle de un medicamento: su información básica y un reporte de
 * disponibilidad por cada farmacia que lo reportó, con su tiempo relativo y lo que dicen las
 * calificaciones recientes.
 *
 * Cómo entenderlo: es como el empleado que arma la ficha de un producto: busca el producto
 * en el catálogo, pregunta en cada tienda cuándo actualizó su información, consulta las
 * últimas reseñas y entrega todo junto en una sola hoja.
 *
 * Detalles técnicos: obtiene el medicamento de MedicamentosRepository, sus reportes de
 * DisponibilidadRepository y las calificaciones recientes de todos los reportes con una sola
 * consulta a CalificacionesRepository. Calcula el texto relativo con TiempoRelativoService y
 * la tendencia de disponibilidad y su mensaje con MensajeDisponibilidadService, usando la hora
 * del servidor como "ahora". Entrega un MedicamentoDetalleDto a MedicamentoDetalleController.
 * Solo lee la base de datos: no guarda ni modifica nada.
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class MedicamentoDetalleServiceImpl implements MedicamentoDetalleService {

  private final MedicamentosRepository medicamentosRepository;
  private final DisponibilidadRepository disponibilidadRepository;
  private final CalificacionesRepository calificacionesRepository;
  private final MensajeDisponibilidadService mensajeDisponibilidadService;
  private final TiempoRelativoService tiempoRelativoService;

  /**
   * Devuelve el detalle de un medicamento.
   *
   * id: id del medicamento.
   *
   * Si el medicamento no existe, lanza MedicamentoNotFoundException. Si existe pero ninguna
   * farmacia lo ha reportado, devuelve el medicamento con la lista de reportes vacía.
   */
  @Override
  public MedicamentoDetalleDto getMedicamentoDetalleById(Long id) {
    MedicamentosEntity medicamento = medicamentosRepository.findById(id)
        .orElseThrow(() -> new MedicamentoNotFoundException(id));

    List<DisponibilidadEntity> disponibilidades = disponibilidadRepository
        .findByMedicamentoIdOrderByFechaHoraReporteDesc(id);
    Map<Integer, List<String>> tiposRecientes = findCalificacionTiposRecientes(disponibilidades);
    LocalDateTime ahora = LocalDateTime.now();

    List<DisponibilidadReporteDto> reportes = disponibilidades.stream()
        .map(disponibilidad -> toDisponibilidadReporteDto(disponibilidad,
            tiposRecientes.getOrDefault(disponibilidad.getId(), List.of()), ahora))
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
  private Map<Integer, List<String>> findCalificacionTiposRecientes(
      List<DisponibilidadEntity> disponibilidades) {
    if (disponibilidades.isEmpty()) {
      return Map.of();
    }
    List<Integer> ids = disponibilidades.stream().map(DisponibilidadEntity::getId).toList();
    return calificacionesRepository
        .findCalificacionesRecientesByDisponibilidad(ids,
            MensajeDisponibilidadService.CALIFICACIONES_RECIENTES)
        .stream()
        .collect(Collectors.groupingBy(CalificacionReciente::getDisponibilidadId,
            Collectors.mapping(CalificacionReciente::getTipo, Collectors.toList())));
  }

  /**
   * Convierte un reporte en el dato que se envía al frontend. Si tiposRecientes está vacía,
   * el reporte no tiene calificaciones y su mensaje es "sin calificaciones aún". Si el reporte
   * tiene más de 15 días, el mensaje lleva además el aviso de que puede estar desactualizado.
   */
  private DisponibilidadReporteDto toDisponibilidadReporteDto(DisponibilidadEntity disponibilidad,
      List<String> tiposRecientes, LocalDateTime ahora) {
    LocalDateTime fecha = disponibilidad.getFechaHoraReporte();
    TendenciaDisponibilidad tendencia = mensajeDisponibilidadService
        .calculateTendenciaDisponibilidad(tiposRecientes);

    return new DisponibilidadReporteDto(disponibilidad.getId(),
        disponibilidad.getFarmacia().getNombreFarmacia(), fecha,
        tiempoRelativoService.getTiempoRelativo(fecha, ahora), tendencia,
        mensajeDisponibilidadService.buildMensajeDisponibilidad(tendencia, fecha, ahora));
  }
}
