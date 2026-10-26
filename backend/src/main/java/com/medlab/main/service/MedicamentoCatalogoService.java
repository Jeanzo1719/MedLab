package com.medlab.main.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medlab.main.dto.CatalogosDto;
import com.medlab.main.dto.GuardarDisponibilidadManualDto;
import com.medlab.main.entity.EstadoDisponibilidad;
import com.medlab.main.exception.DatosInvalidosException;
import com.medlab.main.repository.CategoriaRepository;
import com.medlab.main.repository.FormaFarmaceuticaRepository;
import com.medlab.main.repository.MedicamentoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Catálogo de medicamentos para HU-7: arma las opciones del formulario y reutiliza el
 * medicamento si ya existe, para que dos farmacias no creen duplicados.
 */
@Service
@RequiredArgsConstructor
public class MedicamentoCatalogoService {

  private final MedicamentoRepository medicamentoRepository;
  private final CategoriaRepository categoriaRepository;
  private final FormaFarmaceuticaRepository formaFarmaceuticaRepository;

  @Transactional(readOnly = true)
  public CatalogosDto listar() {
    List<CatalogosDto.Opcion> categorias = categoriaRepository.findAllByOrderByNombreAsc().stream()
        .map(c -> new CatalogosDto.Opcion(c.getId(), c.getNombre())).toList();
    List<CatalogosDto.Opcion> formas = formaFarmaceuticaRepository.findAllByOrderByNombreAsc().stream()
        .map(f -> new CatalogosDto.Opcion(f.getId(), f.getNombre())).toList();
    List<String> estados = List.of(EstadoDisponibilidad.DISPONIBLE, EstadoDisponibilidad.AGOTADO,
        EstadoDisponibilidad.BAJO_PEDIDO);
    return new CatalogosDto(categorias, formas, estados);
  }

  /**
   * Devuelve el id del medicamento con esa combinación única, creándolo si no existe. Si ya
   * existe se respeta su categoría: una farmacia no reescribe el catálogo.
   */
  @Transactional
  public Integer buscarOCrear(GuardarDisponibilidadManualDto datos) {
    // Sin espacios sobrantes, "Dolex  " y "Dolex" no terminan como dos medicamentos distintos.
    String nombre = limpiar(datos.nombreComercial());
    String principio = limpiar(datos.principioActivo());
    String presentacion = limpiar(datos.presentacion());

    // El largo se revisa aquí y no solo en el DTO, porque el CHECK de la BD cuenta lo ya limpio.
    validarLargo("El nombre comercial", nombre, 2, 100);
    validarLargo("El principio activo", principio, 2, 150);
    validarLargo("La presentación", presentacion, 1, 100);

    if (!categoriaRepository.existsById(datos.categoriaId())) {
      throw new DatosInvalidosException("La categoría " + datos.categoriaId() + " no existe");
    }
    if (!formaFarmaceuticaRepository.existsById(datos.formaFarmaceuticaId())) {
      throw new DatosInvalidosException(
          "La forma farmacéutica " + datos.formaFarmaceuticaId() + " no existe");
    }

    medicamentoRepository.crearSiNoExiste(nombre, principio, datos.categoriaId(), presentacion,
        datos.formaFarmaceuticaId());
    return medicamentoRepository
        .buscarIdPorCombinacion(nombre, principio, presentacion, datos.formaFarmaceuticaId())
        .orElseThrow(() -> new IllegalStateException("El medicamento no aparece después de crearlo"));
  }

  private String limpiar(String texto) {
    return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
  }

  private void validarLargo(String campo, String valor, int min, int max) {
    if (valor.length() < min || valor.length() > max) {
      throw new DatosInvalidosException(campo + " debe tener entre " + min + " y " + max + " caracteres");
    }
  }
}