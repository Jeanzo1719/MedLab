package com.medlab.main.dto;

import java.math.BigDecimal;

/**
 * Qué hace: reúne y normaliza los filtros que llegan desde el buscador de la página del
 * paciente/cuidador: texto, campo, farmacia, rango de precio, orden y página.
 *
 * Cómo entenderlo: es como el formulario de búsqueda ya diligenciado y limpio. Si el usuario
 * escribe una sola letra, se ignora; si escribe un precio al revés, se corrige; si no pone
 * tamaño de página, se usa el de siempre.
 *
 * Detalles técnicos: se construye con {@link #desde(String, String, String, String, String,
 * String, String, String)} para que el controller no tenga que parsear ni validar nada. Los
 * encabezados llegan como texto porque vienen por STOMP. Los valores inválidos se convierten
 * en "sin filtro" en vez de lanzar una excepción, así una búsqueda mal formada devuelve una
 * página vacía y no deja al cliente esperando una respuesta que nunca llega.
 *
 * texto: lo que escribió el usuario. Se ignoran menos de 2 caracteres.
 * buscarNombre: si el texto se busca en el nombre comercial.
 * buscarPrincipio: si el texto se busca en el principio activo.
 * farmacia: nombre o parte del nombre de una farmacia aprobada. Se ignoran menos de 2
 * caracteres.
 * precioMin, precioMax: rango de precio. Cualquiera de los dos puede ser null.
 * orden: "relevancia" o "precio". Cualquier otro valor usa relevancia.
 * pagina: página pedida, desde 0.
 * tamano: resultados por página, entre 1 y {@link #TAMANO_MAXIMO}.
 */
public record BusquedaMedicamentosDto(String texto, boolean buscarNombre, boolean buscarPrincipio,
    String farmacia, BigDecimal precioMin, BigDecimal precioMax, String orden, int pagina,
    int tamano) {

  /** Cuántos resultados se devuelven si el cliente no pide un tamaño. */
  public static final int TAMANO_POR_DEFECTO = 20;

  /** El máximo de resultados por página, para que una petición no traiga todo el catálogo. */
  public static final int TAMANO_MAXIMO = 50;

  /** El orden por defecto: primero las coincidencias más cercanas al texto buscado. */
  public static final String ORDEN_RELEVANCIA = "relevancia";

  /** El otro orden posible: del precio más bajo al más alto. */
  public static final String ORDEN_PRECIO = "precio";

  /**
   * Arma los filtros a partir de los encabezados del WebSocket. No lanza excepciones: los
   * datos que no se entienden se descartan.
   */
  public static BusquedaMedicamentosDto desde(String texto, String campo, String farmacia,
      String precioMin, String precioMax, String orden, String pagina, String tamano) {
    String textoLimpio = limpiar(texto);
    if (textoLimpio.length() < 2) {
      textoLimpio = "";
    }

    String farmaciaLimpia = limpiar(farmacia);
    if (farmaciaLimpia.length() < 2) {
      farmaciaLimpia = "";
    }

    boolean buscaNombre = true;
    boolean buscaPrincipio = true;
    String campoLimpio = limpiar(campo).toLowerCase();
    if (!campoLimpio.isEmpty() && !"todos".equals(campoLimpio)) {
      buscaNombre = campoLimpio.contains("nombre");
      buscaPrincipio = campoLimpio.contains("principio");
      if (!buscaNombre && !buscaPrincipio) {
        buscaNombre = true;
        buscaPrincipio = true;
      }
    }

    BigDecimal minimo = parsearPrecio(precioMin);
    BigDecimal maximo = parsearPrecio(precioMax);
    if (minimo != null && maximo != null && minimo.compareTo(maximo) > 0) {
      BigDecimal anterior = minimo;
      minimo = maximo;
      maximo = anterior;
    }

    String ordenLimpio = ORDEN_PRECIO.equalsIgnoreCase(limpiar(orden))
        ? ORDEN_PRECIO
        : ORDEN_RELEVANCIA;

    return new BusquedaMedicamentosDto(textoLimpio, buscaNombre, buscaPrincipio, farmaciaLimpia,
        minimo, maximo, ordenLimpio, parsearPagina(pagina), parsearTamano(tamano));
  }

  /** Si no hay ningún criterio, no tiene sentido consultar toda la tabla. */
  public boolean sinCriterios() {
    return texto.isEmpty() && farmacia.isEmpty() && precioMin == null && precioMax == null;
  }

  /** Cuántas filas se saltan antes de la página pedida. */
  public int desplazamiento() {
    return pagina * tamano;
  }

  private static String limpiar(String valor) {
    return valor == null ? "" : valor.trim();
  }

  private static BigDecimal parsearPrecio(String valor) {
    String limpio = limpiar(valor);
    if (limpio.isEmpty()) {
      return null;
    }
    try {
      BigDecimal precio = new BigDecimal(limpio);
      return precio.signum() < 0 ? null : precio;
    } catch (NumberFormatException excepcion) {
      return null;
    }
  }

  private static int parsearPagina(String valor) {
    String limpio = limpiar(valor);
    if (limpio.isEmpty()) {
      return 0;
    }
    try {
      int pagina = Integer.parseInt(limpio);
      return Math.max(pagina, 0);
    } catch (NumberFormatException excepcion) {
      return 0;
    }
  }

  private static int parsearTamano(String valor) {
    String limpio = limpiar(valor);
    if (limpio.isEmpty()) {
      return TAMANO_POR_DEFECTO;
    }
    try {
      int tamano = Integer.parseInt(limpio);
      if (tamano < 1) {
        return TAMANO_POR_DEFECTO;
      }
      return Math.min(tamano, TAMANO_MAXIMO);
    } catch (NumberFormatException excepcion) {
      return TAMANO_POR_DEFECTO;
    }
  }
}
