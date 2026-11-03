package com.medlab.main.repository;

import com.medlab.main.dto.BusquedaMedicamentosDto;
import com.medlab.main.entity.MedicamentosEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicamentosRepository {

  List<MedicamentosEntity> findAll();

  Page<MedicamentosEntity> findAll(Pageable pageable);

  Optional<MedicamentosEntity> findById(Long id);

  MedicamentosEntity save(MedicamentosEntity medicamentosEntity);

  void deleteById(Long id);

  Optional<MedicamentosEntity> findByNombreComercial(String nombreComercial);

  Optional<MedicamentosEntity> findByPrincipioActivo(String principioActivo);

  List<MedicamentosEntity> findByPrincipioActivoAndIdNot(String principioActivo, Long id);

  List<MedicamentosEntity> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
      String nombreComercial, String principioActivo);

  /**
   * Busca medicamentos con todos los filtros del buscador y devuelve una página con los datos
   * que necesita cada fila: categoría, precio mínimo y conteo de farmacias aprobadas.
   */
  Page<MedicamentoBusquedaProyeccion> buscarMedicamentos(BusquedaMedicamentosDto filtros,
      Pageable pageable);

  /**
   * Una fila del buscador. Es una proyección de la consulta nativa: no es una entidad, solo
   * los campos que se muestran. Los nombres coinciden con los alias de la consulta.
   */
  interface MedicamentoBusquedaProyeccion {

    Long getId();

    String getNombreComercial();

    String getPrincipioActivo();

    String getCategoria();

    String getPresentacion();

    String getFormaFarmaceutica();

    BigDecimal getPrecioMinimo();

    Long getFarmaciasDisponibles();

    Long getFarmaciasReportaron();
  }
}
