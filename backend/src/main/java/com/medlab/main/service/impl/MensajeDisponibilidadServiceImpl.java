package com.medlab.main.service.impl;

import com.medlab.main.dto.TendenciaDisponibilidad;
import com.medlab.main.service.MensajeDisponibilidadService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Qué hace: decide qué dicen las calificaciones recientes de un reporte sobre la
 * disponibilidad del medicamento.
 *
 * Cómo entenderlo: es como contar los votos de los últimos clientes: si la mayoría dice "sí
 * hay", el letrero dice que está disponible; si dos opciones empatan, el letrero dice que no
 * se ponen de acuerdo.
 *
 * Detalles técnicos: cuenta cuántas calificaciones recientes son "Si hay", "Bajo pedido" y
 * "No hay" (escritos igual que en la tabla tipos_calificacion). Gana el tipo con más votos;
 * si dos o más empatan en el primer lugar, devuelve SIN_CONSENSO. Los nombres que no son
 * estos tres se ignoran. Si no queda ningún voto válido, devuelve SIN_CALIFICACIONES.
 * MedicamentoDetalleServiceImpl lo usa una vez por reporte, con las calificaciones que le
 * entrega CalificacionesRepository. No consulta la base de datos.
 */
@Service
public class MensajeDisponibilidadServiceImpl implements MensajeDisponibilidadService {

  @Override
  public TendenciaDisponibilidad calculateTendenciaDisponibilidad(List<String> tiposRecientes) {
    long votosSiHay = countTipoCalificacion(tiposRecientes, TIPO_SI_HAY);
    long votosBajoPedido = countTipoCalificacion(tiposRecientes, TIPO_BAJO_PEDIDO);
    long votosNoHay = countTipoCalificacion(tiposRecientes, TIPO_NO_HAY);

    long votosMaximos = Math.max(votosSiHay, Math.max(votosBajoPedido, votosNoHay));
    if (votosMaximos == 0) {
      return TendenciaDisponibilidad.SIN_CALIFICACIONES;
    }

    int tiposEmpatados = (votosSiHay == votosMaximos ? 1 : 0)
        + (votosBajoPedido == votosMaximos ? 1 : 0)
        + (votosNoHay == votosMaximos ? 1 : 0);
    if (tiposEmpatados > 1) {
      return TendenciaDisponibilidad.SIN_CONSENSO;
    }
    if (votosSiHay == votosMaximos) {
      return TendenciaDisponibilidad.DISPONIBLE;
    }
    if (votosBajoPedido == votosMaximos) {
      return TendenciaDisponibilidad.BAJO_PEDIDO;
    }
    return TendenciaDisponibilidad.NO_DISPONIBLE;
  }

  @Override
  public String buildMensajeDisponibilidad(TendenciaDisponibilidad tendencia,
      LocalDateTime fechaHoraReporte, LocalDateTime ahora) {
    if (!fechaHoraReporte.isBefore(ahora.minusDays(DIAS_REPORTE_VIGENTE))) {
      return tendencia.getMensaje();
    }
    return tendencia.getMensaje() + ". El reporte de la farmacia tiene más de "
        + DIAS_REPORTE_VIGENTE + " días y puede estar desactualizado";
  }

  private long countTipoCalificacion(List<String> tipos, String tipoCalificacion) {
    return tipos.stream().filter(tipoCalificacion::equals).count();
  }
}
