package com.medlab.main.dto;

import java.util.List;

/**
 * Qué hace: lleva la información básica de un medicamento y sus reportes de disponibilidad,
 * uno por farmacia.
 *
 * Cómo entenderlo: es como la ficha de un producto en una tienda en línea: arriba van sus
 * datos y debajo la lista de tiendas que lo tienen, con la última actualización de cada una.
 *
 * Detalles técnicos: MedicamentoDetalleService lo arma con los datos de un Medicamento
 * (incluidos los nombres de su categoría y su forma farmacéutica) y una lista de
 * DisponibilidadReporteDto. MedicamentoDetalleController lo envía al frontend como JSON.
 *
 * id: id del medicamento.
 * nombreComercial: nombre de marca.
 * principioActivo: sustancia que hace efecto.
 * categoria: nombre de la categoría o uso general.
 * presentacion: cómo viene empacado.
 * formaFarmaceutica: nombre de la forma, por ejemplo "Tableta".
 * reportes: un reporte por cada farmacia que reportó el medicamento, del más reciente al
 * más antiguo. La lista está vacía si ninguna farmacia lo ha reportado.
 */
public record MedicamentoDetalleDto(Integer id, String nombreComercial, String principioActivo,
    String categoria, String presentacion, String formaFarmaceutica,
    List<DisponibilidadReporteDto> reportes) {
}