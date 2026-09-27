package com.medlab.main.controller;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.service.MedicamentoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints under /api/public need no authentication (visitor mode). */
@RestController
@RequestMapping("/api/public/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

  private final MedicamentoService medicamentoService;

  @GetMapping
  public List<MedicamentoBusquedaDto> buscar(@RequestParam(name = "q", defaultValue = "") String texto) {
    return medicamentoService.buscar(texto);
  }
}
