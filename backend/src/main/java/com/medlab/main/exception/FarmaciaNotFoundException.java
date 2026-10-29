package com.medlab.main.exception;

/**
 * Qué hace: avisa que se intentó aprobar o rechazar una farmacia que no existe.
 *
 * Cómo entenderlo: es como querer firmar una solicitud que no está en el archivador.
 *
 * Detalles técnicos: la lanza AprobacionFarmaciasServiceImpl cuando FarmaciasRepository no
 * encuentra el id. FarmaciasController la captura y deja un aviso en el log del backend; el
 * cliente no recibe respuesta. El mensaje incluye el id que se buscó.
 */
public class FarmaciaNotFoundException extends RuntimeException {

  public FarmaciaNotFoundException(Integer id) {
    super("No existe la farmacia con id " + id);
  }
}
