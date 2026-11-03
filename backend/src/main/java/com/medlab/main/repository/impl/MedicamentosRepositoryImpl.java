package com.medlab.main.repository.impl;

import com.medlab.main.dto.BusquedaMedicamentosDto;
import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.repository.MedicamentosJpaRepository;
import com.medlab.main.repository.MedicamentosRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MedicamentosRepositoryImpl implements MedicamentosRepository {

  /** Cuando el rango no tiene tope, este valor deja pasar cualquier precio de la base. */
  private static final BigDecimal PRECIO_MAXIMO_SIN_TOPE = new BigDecimal("999999999.99");

  private final MedicamentosJpaRepository jpaRepository;

  @Override
  public List<MedicamentosEntity> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  public Page<MedicamentosEntity> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable);
  }

  @Override
  public Optional<MedicamentosEntity> findById(Long id) {
    return jpaRepository.findById(id);
  }

  @Override
  public MedicamentosEntity save(MedicamentosEntity medicamentosEntity) {
    return jpaRepository.save(medicamentosEntity);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public Optional<MedicamentosEntity> findByNombreComercial(String nombreComercial) {
    return jpaRepository.findByNombreComercial(nombreComercial);
  }

  @Override
  public Optional<MedicamentosEntity> findByPrincipioActivo(String principioActivo) {
    return jpaRepository.findByPrincipioActivo(principioActivo);
  }

  @Override
  public List<MedicamentosEntity> findByPrincipioActivoAndIdNot(String principioActivo, Long id) {
    return jpaRepository.findByPrincipioActivoAndIdNot(principioActivo, id);
  }

  @Override
  public List<MedicamentosEntity> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
      String nombreComercial, String principioActivo) {
    return jpaRepository.findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
        nombreComercial, principioActivo);
  }

  @Override
  public Page<MedicamentoBusquedaProyeccion> buscarMedicamentos(
      BusquedaMedicamentosDto filtros, Pageable pageable) {
    boolean sinPrecio = filtros.precioMin() == null && filtros.precioMax() == null;
    BigDecimal precioMinimo = filtros.precioMin() == null ? BigDecimal.ZERO : filtros.precioMin();
    BigDecimal precioMaximo = filtros.precioMax() == null
        ? PRECIO_MAXIMO_SIN_TOPE
        : filtros.precioMax();

    return jpaRepository.buscarMedicamentos(
        filtros.texto(),
        filtros.buscarNombre() ? 1 : 0,
        filtros.buscarPrincipio() ? 1 : 0,
        filtros.farmacia(),
        sinPrecio ? 1 : 0,
        precioMinimo,
        precioMaximo,
        filtros.orden(),
        pageable);
  }
}
