package com.medlab.main.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medlab.main.dto.MedicamentoRequestDto;
import com.medlab.main.dto.MedicamentoResponseDto;
import com.medlab.main.service.MedicamentosService;
import com.medlab.main.shared.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/medicamentos")
public class MedicamentosController {

  private final MedicamentosService medicamentosService;
  private final SimpMessagingTemplate messagingTemplate;

  @PostMapping
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> createMedicamento(
      @Valid @RequestBody MedicamentoRequestDto medicamentoRequestDto) {
    MedicamentoResponseDto medicamento = medicamentosService.createMedicamento(medicamentoRequestDto);

    ApiResponse<MedicamentoResponseDto> medicamentoResponse = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.CREATED.value())
        .message("Medicamento creado correctamente")
        .data(medicamento)
        .build();

    messagingTemplate.convertAndSend("/ws/medicamentos/creados", medicamento);
    messagingTemplate.convertAndSend("/ws/medicamentos", medicamento);

    return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> getMedicamentoById(@PathVariable Long id) {
    MedicamentoResponseDto medicamento = medicamentosService.getMedicamentoById(id);

    ApiResponse<MedicamentoResponseDto> medicamentoResponse = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento obtenido correctamente")
        .data(medicamento)
        .build();

    return ResponseEntity.ok(medicamentoResponse);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> getAllMedicamentos() {
    List<MedicamentoResponseDto> medicamentos = medicamentosService.getAllMedicamentos();

    ApiResponse<List<MedicamentoResponseDto>> medicamentosResponse = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos listados correctamente")
        .data(medicamentos)
        .build();

    return ResponseEntity.ok(medicamentosResponse);
  }

  @GetMapping("/paginado")
  public ResponseEntity<ApiResponse<Page<MedicamentoResponseDto>>> getMedicamentosPaginated(Pageable pageable) {
    Page<MedicamentoResponseDto> paginaMedicamentos = medicamentosService.getMedicamentosPaginated(pageable);

    ApiResponse<Page<MedicamentoResponseDto>> medicamentosResponse = ApiResponse.<Page<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos paginados correctamente")
        .data(paginaMedicamentos)
        .build();

    return ResponseEntity.ok(medicamentosResponse);
  }

  @GetMapping("/buscar/nombre-comercial")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> getMedicamentoByNombreComercial(
      @RequestParam String nombreComercial) {
    MedicamentoResponseDto medicamento = medicamentosService.getMedicamentoByNombreComercial(nombreComercial);

    ApiResponse<MedicamentoResponseDto> medicamentoResponse = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento encontrado")
        .data(medicamento)
        .build();

    return ResponseEntity.ok(medicamentoResponse);
  }

  @GetMapping("/buscar/principio-activo")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> getMedicamentoByPrincipioActivo(
      @RequestParam String principioActivo) {
    MedicamentoResponseDto medicamento = medicamentosService.getMedicamentoByPrincipioActivo(principioActivo);

    ApiResponse<MedicamentoResponseDto> medicamentoResponse = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento encontrado")
        .data(medicamento)
        .build();

    return ResponseEntity.ok(medicamentoResponse);
  }

  @GetMapping("/buscar/precio")
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> searchMedicamentosByPrecioRange(
      @RequestParam BigDecimal precioMin, @RequestParam BigDecimal precioMax) {
    List<MedicamentoResponseDto> medicamentos = medicamentosService.searchMedicamentosByPrecioRange(precioMin, precioMax);

    ApiResponse<List<MedicamentoResponseDto>> medicamentosResponse = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos encontrados")
        .data(medicamentos)
        .build();

    return ResponseEntity.ok(medicamentosResponse);
  }

  @GetMapping("/buscar/farmacia")
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> searchMedicamentosByFarmacia(
      @RequestParam String nombreFarmacia) {
    List<MedicamentoResponseDto> medicamentos = medicamentosService.searchMedicamentosByFarmacia(nombreFarmacia);

    ApiResponse<List<MedicamentoResponseDto>> medicamentosResponse = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos encontrados")
        .data(medicamentos)
        .build();

    return ResponseEntity.ok(medicamentosResponse);
  }

  @GetMapping("/{id}/recomendaciones")
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> getMedicamentoRecomendaciones(
      @PathVariable Long id) {
    List<MedicamentoResponseDto> recomendaciones = medicamentosService.getMedicamentoRecomendaciones(id);

    ApiResponse<List<MedicamentoResponseDto>> recomendacionesResponse = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Recomendaciones obtenidas correctamente")
        .data(recomendaciones)
        .build();

    return ResponseEntity.ok(recomendacionesResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> updateMedicamento(@PathVariable Long id,
      @Valid @RequestBody MedicamentoRequestDto medicamentoRequestDto) {
    MedicamentoResponseDto medicamentoActualizado = medicamentosService.updateMedicamento(id, medicamentoRequestDto);

    ApiResponse<MedicamentoResponseDto> medicamentoResponse = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento actualizado correctamente")
        .data(medicamentoActualizado)
        .build();

    messagingTemplate.convertAndSend("/ws/medicamentos/actualizados", medicamentoActualizado);
    messagingTemplate.convertAndSend("/ws/medicamentos", medicamentoActualizado);

    return ResponseEntity.ok(medicamentoResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteMedicamento(@PathVariable Long id) {
    medicamentosService.deleteMedicamento(id);

    ApiResponse<Void> medicamentoResponse = ApiResponse.<Void>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento eliminado correctamente")
        .data(null)
        .build();

    messagingTemplate.convertAndSend("/ws/medicamentos/eliminados", id);
    messagingTemplate.convertAndSend("/ws/medicamentos", "deleted:" + id);

    return ResponseEntity.ok(medicamentoResponse);
  }
}
