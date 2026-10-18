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
      @Valid @RequestBody MedicamentoRequestDto dto) {
    MedicamentoResponseDto creado = medicamentosService.crear(dto);

    ApiResponse<MedicamentoResponseDto> response = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.CREATED.value())
        .message("Medicamento creado correctamente")
        .data(creado)
        .build();

    messagingTemplate.convertAndSend("/ws/medicamentos/creados", creado);
    messagingTemplate.convertAndSend("/ws/medicamentos", creado);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> getMedicamentoById(@PathVariable Long id) {
    MedicamentoResponseDto dto = medicamentosService.obtenerPorId(id);

    ApiResponse<MedicamentoResponseDto> response = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento obtenido correctamente")
        .data(dto)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> getAllMedicamentos() {
    List<MedicamentoResponseDto> lista = medicamentosService.listarTodos();

    ApiResponse<List<MedicamentoResponseDto>> response = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos listados correctamente")
        .data(lista)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/buscar/nombre-comercial")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> searchByNombreComercial(
      @RequestParam String nombreComercial) {
    MedicamentoResponseDto dto = medicamentosService.buscarPorNombreComercial(nombreComercial);

    ApiResponse<MedicamentoResponseDto> response = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento encontrado")
        .data(dto)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/buscar/principio-activo")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> searchByPrincipioActivo(
      @RequestParam String principioActivo) {
    MedicamentoResponseDto dto = medicamentosService.buscarPorPrincipioActivo(principioActivo);

    ApiResponse<MedicamentoResponseDto> response = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento encontrado")
        .data(dto)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/buscar/precio")
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> searchByPrecioRange(
      @RequestParam BigDecimal precioMin, @RequestParam BigDecimal precioMax) {
    List<MedicamentoResponseDto> lista = medicamentosService.buscarPorRangoPrecio(precioMin, precioMax);

    ApiResponse<List<MedicamentoResponseDto>> response = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos encontrados")
        .data(lista)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/buscar/farmacia")
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> searchByFarmacia(
      @RequestParam String nombreFarmacia) {
    List<MedicamentoResponseDto> lista = medicamentosService.buscarPorFarmacia(nombreFarmacia);

    ApiResponse<List<MedicamentoResponseDto>> response = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos encontrados")
        .data(lista)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/recomendaciones")
  public ResponseEntity<ApiResponse<List<MedicamentoResponseDto>>> getRecomendaciones(
      @PathVariable Long id) {
    List<MedicamentoResponseDto> lista = medicamentosService.recomendar(id);

    ApiResponse<List<MedicamentoResponseDto>> response = ApiResponse.<List<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Recomendaciones obtenidas correctamente")
        .data(lista)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/paginado")
  public ResponseEntity<ApiResponse<Page<MedicamentoResponseDto>>> getAllMedicamentosPaginado(
      Pageable pageable) {
    Page<MedicamentoResponseDto> pagina = medicamentosService.listarPaginado(pageable);

    ApiResponse<Page<MedicamentoResponseDto>> response = ApiResponse.<Page<MedicamentoResponseDto>>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamentos paginados correctamente")
        .data(pagina)
        .build();

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<MedicamentoResponseDto>> updateData(@PathVariable Long id,
      @Valid @RequestBody MedicamentoRequestDto dto) {
    MedicamentoResponseDto actualizado = medicamentosService.actualizar(id, dto);

    ApiResponse<MedicamentoResponseDto> response = ApiResponse.<MedicamentoResponseDto>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento actualizado correctamente")
        .data(actualizado)
        .build();

    messagingTemplate.convertAndSend("/ws/medicamentos/actualizados", actualizado);
    messagingTemplate.convertAndSend("/ws/medicamentos", actualizado);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteData(@PathVariable Long id) {
    medicamentosService.eliminar(id);

    ApiResponse<Void> response = ApiResponse.<Void>builder()
        .code(HttpStatus.OK.value())
        .message("Medicamento eliminado correctamente")
        .data(null)
        .build();

    messagingTemplate.convertAndSend("/ws/medicamentos/eliminados", id);
    messagingTemplate.convertAndSend("/ws/medicamentos", "deleted:" + id);

    return ResponseEntity.ok(response);
  }
}
