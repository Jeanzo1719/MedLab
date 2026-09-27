package com.medlab.main.service;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.entity.EstadoAprobacion;
import com.medlab.main.entity.Farmacia;
import com.medlab.main.repository.FarmaciaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmaciaService {

  private final FarmaciaRepository farmaciaRepository;

  /** Only approved pharmacies are public; pending and suspended ones stay hidden. */
  public List<FarmaciaMapaDto> listarAprobadasParaMapa() {
    return farmaciaRepository.findByEstadoAprobacion(EstadoAprobacion.APROBADA).stream()
        .map(this::aMapaDto)
        .toList();
  }

  private FarmaciaMapaDto aMapaDto(Farmacia farmacia) {
    return new FarmaciaMapaDto(
        farmacia.getId(), farmacia.getNombreFarmacia(), farmacia.getLatitud(), farmacia.getLongitud());
  }
}
