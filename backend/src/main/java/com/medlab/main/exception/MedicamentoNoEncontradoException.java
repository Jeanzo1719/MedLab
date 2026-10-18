package com.medlab.main.exception;

/**
 * Qué hace: avisa que se pidió el detalle de un medicamento que no existe.
 *
 * Cómo entenderlo: es como la respuesta "no tenemos ese producto" cuando pides algo que no
 * está en el catálogo.
 *
 * Detalles técnicos: la lanza MedicamentoDetalleService cuando MedicamentoRepository no
 * encuentra el id pedido. MedicamentoDetalleController la captura y deja un aviso en el log
 * del backend; el cliente no recibe respuesta. El mensaje incluye el id que se buscó.
 */
public class MedicamentoNoEncontradoException extends RuntimeException {

  public MedicamentoNoEncontradoException(Integer id) {
    super("No existe el medicamento con id " + id);
  }
}