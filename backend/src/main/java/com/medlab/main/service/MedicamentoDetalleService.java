package com.medlab.main.service;

import com.medlab.main.dto.MedicamentoDetalleDto;

/**
 * Qué hace: arma el detalle de un medicamento: su información básica y un reporte de
 * disponibilidad por cada farmacia que lo reportó, con su tiempo relativo y lo que dicen las
 * calificaciones recientes.
 */
public interface MedicamentoDetalleService {

  MedicamentoDetalleDto getMedicamentoDetalleById(Long id);
}
