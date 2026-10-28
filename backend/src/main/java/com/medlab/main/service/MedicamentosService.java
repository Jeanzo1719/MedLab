package com.medlab.main.service;

import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.dto.MedicamentoRequestDto;
import com.medlab.main.dto.MedicamentoResponseDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicamentosService {

    MedicamentoResponseDto createMedicamento(MedicamentoRequestDto medicamentoRequest);

    MedicamentoResponseDto getMedicamentoById(Long id);

    List<MedicamentoResponseDto> getAllMedicamentos();

    Page<MedicamentoResponseDto> getMedicamentosPaginated(Pageable pageable);

    MedicamentoResponseDto updateMedicamento(Long id, MedicamentoRequestDto medicamentoRequest);

    void deleteMedicamento(Long id);

    MedicamentoResponseDto getMedicamentoByNombreComercial(String nombreComercial);

    MedicamentoResponseDto getMedicamentoByPrincipioActivo(String principioActivo);

    List<MedicamentoResponseDto> searchMedicamentosByPrecioRange(BigDecimal precioMin, BigDecimal precioMax);

    List<MedicamentoResponseDto> searchMedicamentosByFarmacia(String nombreFarmacia);

    List<MedicamentoResponseDto> getMedicamentoRecomendaciones(Long id);

    List<MedicamentoBusquedaDto> searchMedicamentosByTexto(String texto);
}
