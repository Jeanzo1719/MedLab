package com.medlab.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medlab.main.entity.Disponibilidad;

/**
 * Qué hace: da acceso a la tabla disponibilidad y trae los reportes que las farmacias han
 * hecho de un medicamento.
 *
 * Cómo entenderlo: es como revisar los estantes de varias farmacias para ver cuáles tienen
 * un medicamento y cuándo actualizaron su información.
 *
 * Detalles técnicos: JpaRepository ya incluye, sin escribir código, estos métodos básicos:
 * - findAll(): todos los reportes (SELECT * FROM disponibilidad).
 * - findById(id): un reporte por su id. Devuelve un Optional porque puede no existir.
 * - count(): cuántos reportes hay en total.
 * - existsById(id): true si existe un reporte con ese id.
 * Además define findByMedicamentoIdConFarmacia, que usa MedicamentoDetalleService para armar
 * el detalle de un medicamento.
 */
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {

  /**
   * Trae los reportes de un medicamento, uno por farmacia, del más reciente al más antiguo.
   * Incluye la farmacia en la misma consulta para poder mostrar su nombre sin consultas
   * adicionales.
   *
   * medicamentoId: id del medicamento.
   *
   * Devuelve una lista vacía si ninguna farmacia ha reportado ese medicamento.
   */
  @Query("""
      select d from Disponibilidad d
      join fetch d.farmacia
      where d.medicamento.id = :medicamentoId
      order by d.fechaHoraReporte desc
      """)
  List<Disponibilidad> findByMedicamentoIdConFarmacia(@Param("medicamentoId") Integer medicamentoId);
}