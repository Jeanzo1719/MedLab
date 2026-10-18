package com.medlab.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medlab.main.entity.Medicamento;

/**
 * Qué hace: da acceso a la tabla medicamentos. Permite buscar medicamentos por texto y
 * traer el detalle completo de uno.
 *
 * Cómo entenderlo: es como el catálogo de una farmacia: puedes preguntar "qué tienen que se
 * parezca a ibu" o pedir la ficha completa de un medicamento concreto.
 *
 * Detalles técnicos: JpaRepository ya incluye, sin escribir código, estos métodos básicos:
 * - findAll(): todos los medicamentos (SELECT * FROM medicamentos).
 * - findById(id): un medicamento por su id. Devuelve un Optional porque puede no existir.
 * - count(): cuántos medicamentos hay en total.
 * - existsById(id): true si existe un medicamento con ese id.
 * Además define dos consultas propias: la búsqueda por texto, que usa MedicamentoService, y
 * el detalle de un medicamento, que usa MedicamentoDetalleService.
 */
public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

  /**
   * Trae un medicamento junto con su categoría y su forma farmacéutica en una sola consulta.
   *
   * id: id del medicamento.
   *
   * Devuelve un Optional vacío si no existe un medicamento con ese id.
   */
  @Query("""
      select m from Medicamento m
      join fetch m.categoria
      join fetch m.formaFarmaceutica
      where m.id = :id
      """)
  Optional<Medicamento> findDetalleById(@Param("id") Integer id);

  /**
   * Busca por nombre comercial o principio activo, sin distinguir mayúsculas
   * (ej. "ibu" encuentra "Ibuprofeno"). Trae la forma farmacéutica de una vez.
   *
   * nombreComercial: texto que debe contener el nombre comercial.
   * principioActivo: texto que debe contener el principio activo.
   *
   * Basta con que coincida uno de los dos campos.
   */
  @EntityGraph(attributePaths = "formaFarmaceutica")
  List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
      String nombreComercial, String principioActivo);
}