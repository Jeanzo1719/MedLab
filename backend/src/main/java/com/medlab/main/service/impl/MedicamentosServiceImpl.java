package com.medlab.main.service.impl;

import com.medlab.main.dto.MedicamentoRequestDto;
import com.medlab.main.dto.MedicamentoResponseDto;
import com.medlab.main.entity.CategoriasEntity;
import com.medlab.main.entity.DisponibilidadEntity;
import com.medlab.main.entity.FormasFarmaceuticasEntity;
import com.medlab.main.entity.MedicamentosEntity;
import com.medlab.main.repository.CategoriasRepository;
import com.medlab.main.repository.DisponibilidadRepository;
import com.medlab.main.repository.FormasFarmaceuticasRepository;
import com.medlab.main.repository.MedicamentosRepository;
import com.medlab.main.service.MedicamentosService;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class MedicamentosServiceImpl implements MedicamentosService {

    private final MedicamentosRepository medicamentosRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final CategoriasRepository categoriasRepository;
    private final FormasFarmaceuticasRepository formasFarmaceuticasRepository;

    // ---------- MAPPERS INLINE ----------
    private MedicamentosEntity toMedicamentoEntity(MedicamentoRequestDto medicamentoRequest) {
        if (medicamentoRequest == null) {
            return null;
        }

        MedicamentosEntity medicamento = new MedicamentosEntity();
        medicamento.setNombreComercial(medicamentoRequest.nombreComercial());
        medicamento.setPrincipioActivo(medicamentoRequest.principioActivo());
        medicamento.setPresentacion(medicamentoRequest.presentacion());

        if (medicamentoRequest.categoriaId() != null) {
            medicamento.setCategoria(getCategoriaById(medicamentoRequest.categoriaId()));
        }

        if (medicamentoRequest.formaFarmaceuticaId() != null) {
            medicamento.setFormaFarmaceutica(getFormaFarmaceuticaById(medicamentoRequest.formaFarmaceuticaId()));
        }

        return medicamento;
    }

    private MedicamentoResponseDto toMedicamentoResponse(MedicamentosEntity medicamento) {
        if (medicamento == null) {
            return null;
        }

        Long categoriaId = null;
        String categoriaNombre = null;
        if (medicamento.getCategoria() != null) {
            categoriaId = medicamento.getCategoria().getId();
            categoriaNombre = medicamento.getCategoria().getNombre();
        }

        Long formaFarmaceuticaId = null;
        String formaFarmaceuticaNombre = null;
        if (medicamento.getFormaFarmaceutica() != null) {
            formaFarmaceuticaId = medicamento.getFormaFarmaceutica().getId();
            formaFarmaceuticaNombre = medicamento.getFormaFarmaceutica().getNombre();
        }

        return new MedicamentoResponseDto(
                medicamento.getId(),
                medicamento.getNombreComercial(),
                medicamento.getPrincipioActivo(),
                categoriaId,
                categoriaNombre,
                medicamento.getPresentacion(),
                formaFarmaceuticaId,
                formaFarmaceuticaNombre
        );
    }

    private void updateMedicamentoEntityFromRequest(MedicamentoRequestDto medicamentoRequest,
            MedicamentosEntity medicamento) {
        if (medicamentoRequest == null || medicamento == null) {
            return;
        }

        medicamento.setNombreComercial(medicamentoRequest.nombreComercial());
        medicamento.setPrincipioActivo(medicamentoRequest.principioActivo());
        medicamento.setPresentacion(medicamentoRequest.presentacion());

        if (medicamentoRequest.categoriaId() != null) {
            medicamento.setCategoria(getCategoriaById(medicamentoRequest.categoriaId()));
        } else {
            medicamento.setCategoria(null);
        }

        if (medicamentoRequest.formaFarmaceuticaId() != null) {
            medicamento.setFormaFarmaceutica(getFormaFarmaceuticaById(medicamentoRequest.formaFarmaceuticaId()));
        } else {
            medicamento.setFormaFarmaceutica(null);
        }
    }

    private CategoriasEntity getCategoriaById(Long id) {
        return categoriasRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con id: " + id));
    }

    private FormasFarmaceuticasEntity getFormaFarmaceuticaById(Long id) {
        return formasFarmaceuticasRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Forma farmacéutica no encontrada con id: " + id));
    }

    private List<MedicamentoResponseDto> toMedicamentoResponseList(List<MedicamentosEntity> medicamentos) {
        if (medicamentos == null) {
            return List.of();
        }

        return medicamentos.stream()
                .map(this::toMedicamentoResponse)
                .toList();
    }

    @Override
    @Transactional
    public MedicamentoResponseDto createMedicamento(MedicamentoRequestDto medicamentoRequest) {
        MedicamentosEntity medicamento = toMedicamentoEntity(medicamentoRequest);
        MedicamentosEntity medicamentoCreado = medicamentosRepository.save(medicamento);
        return toMedicamentoResponse(medicamentoCreado);
    }

    @Override
    public MedicamentoResponseDto getMedicamentoById(Long id) {
        MedicamentosEntity medicamento = medicamentosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con id: " + id));
        return toMedicamentoResponse(medicamento);
    }

    @Override
    public List<MedicamentoResponseDto> getAllMedicamentos() {
        return toMedicamentoResponseList(medicamentosRepository.findAll());
    }

    @Override
    public Page<MedicamentoResponseDto> getMedicamentosPaginated(Pageable pageable) {
        return medicamentosRepository.findAll(pageable).map(this::toMedicamentoResponse);
    }

    @Override
    @Transactional
    public MedicamentoResponseDto updateMedicamento(Long id, MedicamentoRequestDto medicamentoRequest) {
        MedicamentosEntity medicamento = medicamentosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con id: " + id));
        updateMedicamentoEntityFromRequest(medicamentoRequest, medicamento);
        MedicamentosEntity medicamentoActualizado = medicamentosRepository.save(medicamento);
        return toMedicamentoResponse(medicamentoActualizado);
    }

    @Override
    @Transactional
    public void deleteMedicamento(Long id) {
        if (medicamentosRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Medicamento no encontrado con id: " + id);
        }
        medicamentosRepository.deleteById(id);
    }

    @Override
    public MedicamentoResponseDto getMedicamentoByNombreComercial(String nombreComercial) {
        MedicamentosEntity medicamento = medicamentosRepository.findByNombreComercial(nombreComercial)
                .orElseThrow(() -> new NoSuchElementException(
                        "Medicamento no encontrado con nombre comercial: " + nombreComercial));
        return toMedicamentoResponse(medicamento);
    }

    @Override
    public MedicamentoResponseDto getMedicamentoByPrincipioActivo(String principioActivo) {
        MedicamentosEntity medicamento = medicamentosRepository.findByPrincipioActivo(principioActivo)
                .orElseThrow(() -> new NoSuchElementException(
                        "Medicamento no encontrado con principio activo: " + principioActivo));
        return toMedicamentoResponse(medicamento);
    }

    @Override
    public List<MedicamentoResponseDto> searchMedicamentosByPrecioRange(BigDecimal precioMin, BigDecimal precioMax) {
        List<DisponibilidadEntity> disponibilidades =
                disponibilidadRepository.findByPrecioBetween(precioMin, precioMax);

        // Un mismo medicamento puede estar en varias farmacias: se conserva el
        // precio minimo por medicamento y se ordena el resultado por ese precio.
        Map<Long, DisponibilidadEntity> mejorPrecioPorMedicamento = new LinkedHashMap<>();
        for (DisponibilidadEntity disponibilidad : disponibilidades) {
            Long medicamentoId = disponibilidad.getMedicamento().getId();
            DisponibilidadEntity disponibilidadActual = mejorPrecioPorMedicamento.get(medicamentoId);
            if (disponibilidadActual == null || disponibilidad.getPrecio().compareTo(disponibilidadActual.getPrecio()) < 0) {
                mejorPrecioPorMedicamento.put(medicamentoId, disponibilidad);
            }
        }

        if (mejorPrecioPorMedicamento.isEmpty()) {
            throw new NoSuchElementException("No se encontraron medicamentos en el rango de precio indicado");
        }

        return mejorPrecioPorMedicamento.values().stream()
                .sorted(Comparator.comparing(DisponibilidadEntity::getPrecio))
                .map(disponibilidad -> toMedicamentoResponse(disponibilidad.getMedicamento()))
                .toList();
    }

    @Override
    public List<MedicamentoResponseDto> searchMedicamentosByFarmacia(String nombreFarmacia) {
        List<MedicamentosEntity> medicamentos =
                disponibilidadRepository.findMedicamentosByNombreFarmaciaAprobada(nombreFarmacia);

        if (medicamentos.isEmpty()) {
            throw new NoSuchElementException("No se encontraron medicamentos para la farmacia indicada");
        }

        return toMedicamentoResponseList(medicamentos);
    }

    @Override
    public List<MedicamentoResponseDto> getMedicamentoRecomendaciones(Long id) {
        MedicamentosEntity medicamentoBuscado = medicamentosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con id: " + id));

        // Mismo principio activo, excluyendo el medicamento buscado.
        List<MedicamentosEntity> recomendaciones = medicamentosRepository
                .findByPrincipioActivoAndIdNot(medicamentoBuscado.getPrincipioActivo(), id);

        return toMedicamentoResponseList(recomendaciones);
    }
}
