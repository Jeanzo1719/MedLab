package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medlab.main.dto.TendenciaDisponibilidad;

/**
 * Qué hace: decide qué dicen las calificaciones recientes de un reporte sobre la
 * disponibilidad del medicamento.
 *
 * Cómo entenderlo: es como contar los votos de los últimos clientes: si la mayoría dice "sí
 * hay", el letrero dice que está disponible; si dos opciones empatan, el letrero dice que no
 * se ponen de acuerdo.
 *
 * Detalles técnicos: recibe los nombres de los tipos de calificación recientes de un reporte
 * y cuenta cuántas son "Si hay", "Bajo pedido" y "No hay" (escritos igual que en la tabla
 * tipos_calificacion). Gana el tipo con más votos; si dos o más empatan en el primer lugar,
 * devuelve SIN_CONSENSO. Los nombres que no son estos tres se ignoran. Si no queda ningún
 * voto válido, devuelve SIN_CALIFICACIONES. MedicamentoDetalleService lo usa una vez por
 * reporte, con las calificaciones que le entrega CalificacionRepository. No consulta la base
 * de datos.
 */
@Service
public class MensajeDisponibilidadService {

  /** Cuántas calificaciones, las más recientes de cada reporte, se tienen en cuenta. */
  public static final int CALIFICACIONES_RECIENTES = 20;

  /** Nombres de los tipos de calificación, escritos igual que en la base de datos. */
  public static final String TIPO_SI_HAY = "Si hay";
  public static final String TIPO_BAJO_PEDIDO = "Bajo pedido";
  public static final String TIPO_NO_HAY = "No hay";

  /**
   * Calcula la tendencia de disponibilidad de un reporte.
   *
   * tiposRecientes: nombres de los tipos de las calificaciones recientes del reporte. Puede
   * estar vacía.
   */
  public TendenciaDisponibilidad calcular(List<String> tiposRecientes) {
    long siHay = contar(tiposRecientes, TIPO_SI_HAY);
    long bajoPedido = contar(tiposRecientes, TIPO_BAJO_PEDIDO);
    long noHay = contar(tiposRecientes, TIPO_NO_HAY);

    long mayor = Math.max(siHay, Math.max(bajoPedido, noHay));
    if (mayor == 0) {
      return TendenciaDisponibilidad.SIN_CALIFICACIONES;
    }

    int empatados = (siHay == mayor ? 1 : 0) + (bajoPedido == mayor ? 1 : 0)
        + (noHay == mayor ? 1 : 0);
    if (empatados > 1) {
      return TendenciaDisponibilidad.SIN_CONSENSO;
    }
    if (siHay == mayor) {
      return TendenciaDisponibilidad.DISPONIBLE;
    }
    if (bajoPedido == mayor) {
      return TendenciaDisponibilidad.BAJO_PEDIDO;
    }
    return TendenciaDisponibilidad.NO_DISPONIBLE;
  }

  private long contar(List<String> tipos, String tipo) {
    return tipos.stream().filter(tipo::equals).count();
  }
}