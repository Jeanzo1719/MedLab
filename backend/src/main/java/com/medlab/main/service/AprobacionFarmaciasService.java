package com.medlab.main.service;

/**
 * Qué hace: aprueba o rechaza las farmacias que esperan la decisión del administrador y deja
 * registrado cada cambio.
 *
 * Cómo entenderlo: es como la ventanilla que revisa las solicitudes: firma "aprobada" o
 * "rechazada" y anota en la bitácora quién lo hizo y cuándo.
 */
public interface AprobacionFarmaciasService {

  /** Nombres de los estados de farmacia, escritos igual que en la tabla estados_farmacia. */
  String ESTADO_PENDIENTE = "pendiente";
  String ESTADO_APROBADA = "aprobada";
  String ESTADO_RECHAZADA = "rechazada";

  /**
   * Cambia una farmacia pendiente a "aprobada" y registra el cambio.
   *
   * farmaciaId: id de la farmacia.
   * usuarioAdminId: id del administrador que decide. Puede ser null mientras no exista el
   * inicio de sesión.
   *
   * Si la farmacia no existe, lanza FarmaciaNotFoundException. Si no está pendiente, lanza
   * FarmaciaNotPendienteException y no cambia nada.
   */
  void approveFarmacia(Integer farmaciaId, Integer usuarioAdminId);

  /**
   * Cambia una farmacia pendiente a "rechazada" y registra el cambio. Recibe lo mismo y lanza
   * las mismas excepciones que approveFarmacia.
   */
  void rejectFarmacia(Integer farmaciaId, Integer usuarioAdminId);
}
