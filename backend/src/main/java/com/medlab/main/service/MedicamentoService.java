package com.medlab.main.service;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.entity.Medicamento;
import com.medlab.main.repository.MedicamentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

  /** Same minimum length the medicine name has in the data model */
  private static final int LONGITUD_MINIMA_BUSQUEDA = 2;

  private final MedicamentoRepository medicamentoRepository;

  /** Searches by commercial name or active ingredient; short queries return nothing. */
  public List<MedicamentoBusquedaDto> buscar(String texto) {
    String busqueda = texto == null ? "" : texto.strip();
    if (busqueda.length() < LONGITUD_MINIMA_BUSQUEDA) {
      return List.of();
    }

    return medicamentoRepository
        .findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(busqueda, busqueda)
        .stream()
        .map(this::aBusquedaDto)
        .toList();
  }

  private MedicamentoBusquedaDto aBusquedaDto(Medicamento medicamento) {
    return new MedicamentoBusquedaDto(
        medicamento.getId(),
        medicamento.getNombreComercial(),
        medicamento.getPrincipioActivo(),
        medicamento.getPresentacion(),
        medicamento.getFormaFarmaceutica());
  }
}
