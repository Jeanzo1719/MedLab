package com.medlab.main.service;

import com.medlab.main.dto.MedicamentoRequestDto;
import com.medlab.main.dto.MedicamentoResponseDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicamentosService {

    MedicamentoResponseDto crear(MedicamentoRequestDto dto);

    MedicamentoResponseDto obtenerPorId(Long id);

    List<MedicamentoResponseDto> listarTodos();

    Page<MedicamentoResponseDto> listarPaginado(Pageable pageable);

    MedicamentoResponseDto actualizar(Long id, MedicamentoRequestDto dto);

    void eliminar(Long id);

    MedicamentoResponseDto buscarPorNombreComercial(String nombreComercial);

    MedicamentoResponseDto buscarPorPrincipioActivo(String principioActivo);

    List<MedicamentoResponseDto> buscarPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax);

    List<MedicamentoResponseDto> buscarPorFarmacia(String nombreFarmacia);

    List<MedicamentoResponseDto> recomendar(Long id);
}
