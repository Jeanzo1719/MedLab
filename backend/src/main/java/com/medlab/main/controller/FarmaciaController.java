package com.medlab.main.controller;

import com.medlab.main.dto.FarmaciaMapaDto;
import com.medlab.main.service.FarmaciaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints under /api/public need no authentication (visitor mode). */
@RestController
@RequestMapping("/api/public/farmacias")
@RequiredArgsConstructor
public class FarmaciaController {

  private final FarmaciaService farmaciaService;

  @GetMapping("/aprobadas")
  public List<FarmaciaMapaDto> listarAprobadas() {
    return farmaciaService.listarAprobadasParaMapa();
  }
}
