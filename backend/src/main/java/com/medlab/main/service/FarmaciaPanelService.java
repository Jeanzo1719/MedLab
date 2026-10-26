package com.medlab.main.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.DatosFarmaciaDto;
import com.medlab.main.dto.GuardarDatosFarmaciaDto;
import com.medlab.main.entity.EstadoFarmacia;
import com.medlab.main.entity.Farmacia;
import com.medlab.main.exception.AccesoPanelDenegadoException;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.repository.FarmaciaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Datos de la propia farmacia en el panel (HU-11). Solo se editan nombre, dirección, horario y
 * ubicación; los datos legales los revisa un administrador y no se tocan desde aquí.
 */
@Service
@RequiredArgsConstructor
public class FarmaciaPanelService {

  private static final String ESTADO_FARMACIA_APROBADA = "aprobada";

  private final FarmaciaRepository farmaciaRepository;

  @Transactional(readOnly = true)
  public DatosFarmaciaDto obtener(Integer farmaciaId) {
    return aDto(buscarAprobada(farmaciaId));
  }

  @Transactional
  public DatosFarmaciaDto actualizar(Integer farmaciaId, GuardarDatosFarmaciaDto datos) {
    // Un solo extremo dejaría el marcador a medio mover (latitud nueva con longitud vieja).
    if ((datos.latitud() == null) != (datos.longitud() == null)) {
      throw new DatosInvalidosException("La latitud y la longitud van juntas: envía las dos o ninguna");
    }

    Farmacia farmacia = buscarAprobada(farmaciaId);
    farmacia.setNombreFarmacia(datos.nombreFarmacia().trim());
    farmacia.setTipoViaPrincipal(datos.tipoViaPrincipal().trim());
    farmacia.setNumeroViaPrincipal(datos.numeroViaPrincipal().trim());
    farmacia.setLetraViaPrincipal(letra(datos.letraViaPrincipal()));
    farmacia.setNumeroViaGeneradora(datos.numeroViaGeneradora().trim());
    farmacia.setLetraViaGeneradora(letra(datos.letraViaGeneradora()));
    farmacia.setNumeroPredio(datos.numeroPredio().trim());
    farmacia.setComplemento(opcional(datos.complemento()));
    farmacia.setHorarioAtencion(opcional(datos.horarioAtencion()));

    if (datos.latitud() != null) {
      farmacia.setLatitud(datos.latitud());
      farmacia.setLongitud(datos.longitud());
    }

    // Hora del servidor, en segundos: la columna no guarda fracciones y así la respuesta
    // coincide con lo que queda en la BD.
    farmacia.setFechaUltimaActualizacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    return aDto(farmacia);
  }

  /** Mismo criterio que el inventario: una farmacia pendiente o suspendida no usa el panel. */
  private Farmacia buscarAprobada(Integer farmaciaId) {
    Farmacia farmacia = farmaciaRepository.findById(farmaciaId)
        .orElseThrow(() -> new AccesoPanelDenegadoException("La farmacia no existe"));
    EstadoFarmacia estado = farmacia.getEstadoAprobacion();
    if (estado == null || !ESTADO_FARMACIA_APROBADA.equals(estado.getNombre())) {
      throw new AccesoPanelDenegadoException("La farmacia no está aprobada");
    }
    return farmacia;
  }

  /** Mayúscula siempre, para que "5a" y "5A" no sean direcciones distintas. */
  private String letra(String valor) {
    String limpio = opcional(valor);
    return limpio == null ? null : limpio.toUpperCase(Locale.ROOT);
  }

  /** Vacío y solo espacios se guardan como null, no como texto en blanco. */
  private String opcional(String valor) {
    if (valor == null || valor.isBlank()) {
      return null;
    }
    return valor.trim();
  }

  private String armarDireccion(Farmacia f) {
    StringBuilder direccion = new StringBuilder()
        .append(f.getTipoViaPrincipal()).append(' ').append(f.getNumeroViaPrincipal())
        .append(f.getLetraViaPrincipal() == null ? "" : f.getLetraViaPrincipal())
        .append(" # ").append(f.getNumeroViaGeneradora())
        .append(f.getLetraViaGeneradora() == null ? "" : f.getLetraViaGeneradora())
        .append(" - ").append(f.getNumeroPredio());
    if (f.getComplemento() != null) {
      direccion.append(", ").append(f.getComplemento());
    }
    return direccion.toString();
  }

  private DatosFarmaciaDto aDto(Farmacia f) {
    return new DatosFarmaciaDto(f.getUsuarioId(), f.getNombreFarmacia(), f.getTipoViaPrincipal(),
        f.getNumeroViaPrincipal(), f.getLetraViaPrincipal(), f.getNumeroViaGeneradora(),
        f.getLetraViaGeneradora(), f.getNumeroPredio(), f.getComplemento(), armarDireccion(f),
        f.getHorarioAtencion(), f.getLatitud(), f.getLongitud(),
        f.getEstadoAprobacion().getNombre(), f.getFechaUltimaActualizacion());
  }
}