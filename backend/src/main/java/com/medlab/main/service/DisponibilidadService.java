package com.medlab.main.service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.medlab.main.dto.DisponibilidadFarmaciaDto;
import com.medlab.main.dto.GuardarDisponibilidadDto;
import com.medlab.main.dto.ResumenInventarioDto;
import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.EstadoDisponibilidad;
import com.medlab.main.entity.EstadosFarmaciaEntity;
import com.medlab.main.entity.FarmaciasEntity;
import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.exception.RecursoNoEncontradoException;
import com.medlab.main.repository.DisponibilidadJpaRepository;
import com.medlab.main.repository.EstadoDisponibilidadRepository;
import com.medlab.main.repository.FarmaciasJpaRepository;
import com.medlab.main.repository.MedicamentosJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inventario de una farmacia (HU-11). HU-7 debe usar guardar() y no escribir por su cuenta:
 * así hay un solo lugar que valida y avisa a las pantallas en vivo.
 *
 * Toda operación recibe el id de la farmacia y filtra por él, así una farmacia nunca ve ni
 * toca filas de otra.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisponibilidadService {

  /** Por debajo de esta cantidad se marca stock bajo. Es decisión de negocio, no está en la BD. */
  public static final int UMBRAL_STOCK_BAJO = 5;

  private static final String ESTADO_FARMACIA_APROBADA = "aprobada";
  private static final int TAMANO_MAXIMO_PAGINA = 50;
  private static final Set<String> ESTADOS_VALIDOS = Set.of(EstadoDisponibilidad.DISPONIBLE,
      EstadoDisponibilidad.AGOTADO, EstadoDisponibilidad.BAJO_PEDIDO);

  private final DisponibilidadJpaRepository disponibilidadRepository;
  private final EstadoDisponibilidadRepository estadoDisponibilidadRepository;
  private final FarmaciasJpaRepository farmaciaRepository;
  private final MedicamentosJpaRepository medicamentoRepository;
  private final MedicamentoDetalleNotificador notificador;
  private final ActividadFarmaciaService actividadFarmacia;

  /** Crea el producto o actualiza el existente (misma pareja farmacia-medicamento). */
  @Transactional
  public DisponibilidadFarmaciaDto guardar(Integer farmaciaId, GuardarDisponibilidadDto datos) {
    exigirFarmaciaAprobada(farmaciaId);
    String nombreEstado = resolverEstado(datos.estado(), datos.cantidad());

    Long medicamentoId = Long.valueOf(datos.medicamentoId());
    if (!medicamentoRepository.existsById(medicamentoId)) {
      throw new RecursoNoEncontradoException("El medicamento " + medicamentoId + " no existe");
    }

    EstadoDisponibilidad estado = buscarEstado(nombreEstado);
    disponibilidadRepository.guardar(farmaciaId, medicamentoId, datos.cantidad(),
        datos.precio(), estado.getId());
    // Mueve la fecha de la farmacia y atiende sus alertas de inactividad (HU-9).
    actividadFarmacia.registrarActividad(farmaciaId);
    publicarTrasCommit(medicamentoId);

    return disponibilidadRepository
        .findByFarmaciaUsuarioIdAndMedicamentoId(farmaciaId, medicamentoId)
        .map(this::aDto)
        .orElseThrow(() -> new IllegalStateException("La fila no aparece después de guardarla"));
  }

  /** Productos de la farmacia, por orden alfabético; texto vacío trae todos. */
  @Transactional(readOnly = true)
  public Page<DisponibilidadFarmaciaDto> listar(Integer farmaciaId, String texto, int pagina, int tamano) {
    exigirFarmaciaAprobada(farmaciaId);
    String filtro = texto == null ? "" : texto.trim();
    int tamanoSeguro = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO_PAGINA);
    PageRequest paginacion = PageRequest.of(Math.max(pagina, 0), tamanoSeguro,
        Sort.by(Sort.Order.asc("medicamento.nombreComercial").ignoreCase()));

    return disponibilidadRepository.buscarPorFarmacia(farmaciaId, filtro, paginacion).map(this::aDto);
  }

  /**
   * Baja lógica: el producto queda en cero y agotado, no se borra. Así conserva su precio y las
   * calificaciones que los pacientes ya le hicieron.
   */
  @Transactional
  public void marcarAgotado(Integer farmaciaId, Integer medicamentoId) {
    exigirFarmaciaAprobada(farmaciaId);
    EstadoDisponibilidad agotado = buscarEstado(EstadoDisponibilidad.AGOTADO);
    Long idMedicamento = Long.valueOf(medicamentoId);

    int filas = disponibilidadRepository.marcarAgotado(farmaciaId, idMedicamento, agotado.getId());
    if (filas == 0) {
      throw new RecursoNoEncontradoException("La farmacia no maneja el medicamento " + medicamentoId);
    }
    actividadFarmacia.registrarActividad(farmaciaId);
    publicarTrasCommit(idMedicamento);
  }

  /** Cuántos productos hay en cada estado, para el dashboard. */
  @Transactional(readOnly = true)
  public ResumenInventarioDto resumen(Integer farmaciaId) {
    exigirFarmaciaAprobada(farmaciaId);

    Map<String, Long> porEstado = new LinkedHashMap<>();
    ESTADOS_VALIDOS.forEach(estado -> porEstado.put(estado, 0L));
    disponibilidadRepository.contarPorEstado(farmaciaId)
        .forEach(conteo -> porEstado.put(conteo.getEstado(), conteo.getTotal()));

    long total = porEstado.values().stream().mapToLong(Long::longValue).sum();
    return new ResumenInventarioDto(porEstado, total);
  }

  /** Solo farmacias aprobadas usan el panel; una pendiente o suspendida no debe tocar el inventario. */
  private void exigirFarmaciaAprobada(Integer farmaciaId) {
    FarmaciasEntity farmacia = farmaciaRepository.findById(farmaciaId)
        .orElseThrow(() -> new AccesoPanelDenegadoException("La farmacia no existe"));
    EstadosFarmaciaEntity estado = farmacia.getEstadoAprobacion();
    if (estado == null || !ESTADO_FARMACIA_APROBADA.equals(estado.getNombre())) {
      throw new AccesoPanelDenegadoException("La farmacia no está aprobada");
    }
  }

  /**
   * Si no se pide estado, se deduce de la cantidad. Si se pide, debe ser coherente: HU-5 busca
   * farmacias excluyendo las agotadas, y un "disponible" con 0 unidades (o un "agotado" con
   * unidades) mostraría datos falsos al paciente. "bajo_pedido" sí admite cualquier cantidad.
   */
  private String resolverEstado(String estadoPedido, int cantidad) {
    if (estadoPedido == null || estadoPedido.isBlank()) {
      return cantidad == 0 ? EstadoDisponibilidad.AGOTADO : EstadoDisponibilidad.DISPONIBLE;
    }

    String estado = estadoPedido.trim().toLowerCase(Locale.ROOT);
    if (!ESTADOS_VALIDOS.contains(estado)) {
      throw new DatosInvalidosException("Estado no válido: " + estadoPedido);
    }
    if (EstadoDisponibilidad.DISPONIBLE.equals(estado) && cantidad == 0) {
      throw new DatosInvalidosException("Un producto con cantidad 0 no puede estar disponible");
    }
    if (EstadoDisponibilidad.AGOTADO.equals(estado) && cantidad > 0) {
      throw new DatosInvalidosException("Un producto agotado debe tener cantidad 0");
    }
    return estado;
  }

  private EstadoDisponibilidad buscarEstado(String nombre) {
    return estadoDisponibilidadRepository.findByNombre(nombre)
        .orElseThrow(() -> new IllegalStateException("Falta el estado '" + nombre + "' en la base de datos"));
  }

  /**
   * El aviso en vivo sale hasta que la transacción termina: si saliera antes, un cliente podría
   * pedir el detalle y todavía ver el valor viejo. Si el aviso falla, el guardado ya ocurrió y
   * no debe devolverse como error a la farmacia.
   */
  private void publicarTrasCommit(Long medicamentoId) {
    Runnable aviso = () -> {
      try {
        notificador.publishMedicamentoDetalleUpdate(medicamentoId);
      } catch (RuntimeException e) {
        log.warn("No se pudo avisar el cambio del medicamento {}", medicamentoId, e);
      }
    };

    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          aviso.run();
        }
      });
    } else {
      aviso.run();
    }
  }

  private DisponibilidadFarmaciaDto aDto(DisponibilidadEntity d) {
    MedicamentosEntity m = d.getMedicamento();
    int cantidad = d.getCantidadDisponible();
    return new DisponibilidadFarmaciaDto(Math.toIntExact(m.getId()), m.getNombreComercial(),
        m.getPrincipioActivo(), m.getPresentacion(), cantidad, d.getPrecio(),
        d.getEstadoDisponibilidad().getNombre(),
        cantidad > 0 && cantidad < UMBRAL_STOCK_BAJO, d.getFechaHoraReporte());
  }
}