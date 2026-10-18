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
    private MedicamentosEntity toEntity(MedicamentoRequestDto dto) {
        if (dto == null) {
            return null;
        }

        MedicamentosEntity entity = new MedicamentosEntity();
        entity.setNombreComercial(dto.nombreComercial());
        entity.setPrincipioActivo(dto.principioActivo());
        entity.setPresentacion(dto.presentacion());

        if (dto.categoriaId() != null) {
            entity.setCategoria(buscarCategoria(dto.categoriaId()));
        }

        if (dto.formaFarmaceuticaId() != null) {
            entity.setFormaFarmaceutica(buscarFormaFarmaceutica(dto.formaFarmaceuticaId()));
        }

        return entity;
    }

    private MedicamentoResponseDto toResponse(MedicamentosEntity entity) {
        if (entity == null) {
            return null;
        }

        Long categoriaId = null;
        String categoriaNombre = null;
        if (entity.getCategoria() != null) {
            categoriaId = entity.getCategoria().getId();
            categoriaNombre = entity.getCategoria().getNombre();
        }

        Long formaId = null;
        String formaNombre = null;
        if (entity.getFormaFarmaceutica() != null) {
            formaId = entity.getFormaFarmaceutica().getId();
            formaNombre = entity.getFormaFarmaceutica().getNombre();
        }

        return new MedicamentoResponseDto(
                entity.getId(),
                entity.getNombreComercial(),
                entity.getPrincipioActivo(),
                categoriaId,
                categoriaNombre,
                entity.getPresentacion(),
                formaId,
                formaNombre
        );
    }

    private void updateEntityFromDto(MedicamentoRequestDto dto, MedicamentosEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setNombreComercial(dto.nombreComercial());
        entity.setPrincipioActivo(dto.principioActivo());
        entity.setPresentacion(dto.presentacion());

        if (dto.categoriaId() != null) {
            entity.setCategoria(buscarCategoria(dto.categoriaId()));
        } else {
            entity.setCategoria(null);
        }

        if (dto.formaFarmaceuticaId() != null) {
            entity.setFormaFarmaceutica(buscarFormaFarmaceutica(dto.formaFarmaceuticaId()));
        } else {
            entity.setFormaFarmaceutica(null);
        }
    }

    private CategoriasEntity buscarCategoria(Long id) {
        return categoriasRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con id: " + id));
    }

    private FormasFarmaceuticasEntity buscarFormaFarmaceutica(Long id) {
        return formasFarmaceuticasRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Forma farmacéutica no encontrada con id: " + id));
    }

    private List<MedicamentoResponseDto> toResponseList(List<MedicamentosEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MedicamentoResponseDto crear(MedicamentoRequestDto dto) {
        MedicamentosEntity entity = toEntity(dto);
        MedicamentosEntity saved = medicamentosRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public MedicamentoResponseDto obtenerPorId(Long id) {
        MedicamentosEntity entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con id: " + id));
        return toResponse(entity);
    }

    @Override
    public List<MedicamentoResponseDto> listarTodos() {
        return toResponseList(medicamentosRepository.findAll());
    }

    @Override
    public Page<MedicamentoResponseDto> listarPaginado(Pageable pageable) {
        return medicamentosRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public MedicamentoResponseDto actualizar(Long id, MedicamentoRequestDto dto) {
        MedicamentosEntity entity = medicamentosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con id: " + id));
        updateEntityFromDto(dto, entity);
        MedicamentosEntity updated = medicamentosRepository.save(entity);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (medicamentosRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Medicamento no encontrado con id: " + id);
        }
        medicamentosRepository.deleteById(id);
    }

    @Override
    public MedicamentoResponseDto buscarPorNombreComercial(String nombreComercial) {
        MedicamentosEntity entity = medicamentosRepository.findByNombreComercial(nombreComercial)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con nombre comercial: " + nombreComercial));
        return toResponse(entity);
    }

    @Override
    public MedicamentoResponseDto buscarPorPrincipioActivo(String principioActivo) {
        MedicamentosEntity entity = medicamentosRepository.findByPrincipioActivo(principioActivo)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con principio activo: " + principioActivo));
        return toResponse(entity);
    }

    @Override
    public List<MedicamentoResponseDto> buscarPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        List<DisponibilidadEntity> disponibilidades =
                disponibilidadRepository.findByPrecioBetween(precioMin, precioMax);

        // Un mismo medicamento puede estar en varias farmacias: se conserva el
        // precio minimo por medicamento y se ordena el resultado por ese precio.
        Map<Long, DisponibilidadEntity> mejorPrecioPorMedicamento = new LinkedHashMap<>();
        for (DisponibilidadEntity disponibilidad : disponibilidades) {
            Long medicamentoId = disponibilidad.getMedicamento().getId();
            DisponibilidadEntity actual = mejorPrecioPorMedicamento.get(medicamentoId);
            if (actual == null || disponibilidad.getPrecio().compareTo(actual.getPrecio()) < 0) {
                mejorPrecioPorMedicamento.put(medicamentoId, disponibilidad);
            }
        }

        if (mejorPrecioPorMedicamento.isEmpty()) {
            throw new NoSuchElementException("No se encontraron medicamentos en el rango de precio indicado");
        }

        return mejorPrecioPorMedicamento.values().stream()
                .sorted(Comparator.comparing(DisponibilidadEntity::getPrecio))
                .map(disponibilidad -> toResponse(disponibilidad.getMedicamento()))
                .toList();
    }

    @Override
    public List<MedicamentoResponseDto> buscarPorFarmacia(String nombreFarmacia) {
        List<MedicamentosEntity> medicamentos =
                disponibilidadRepository.findMedicamentosByNombreFarmaciaAprobada(nombreFarmacia);

        if (medicamentos.isEmpty()) {
            throw new NoSuchElementException("No se encontraron medicamentos para la farmacia indicada");
        }

        return toResponseList(medicamentos);
    }

    @Override
    public List<MedicamentoResponseDto> recomendar(Long id) {
        MedicamentosEntity buscado = medicamentosRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Medicamento no encontrado con id: " + id));

        // Mismo principio activo, excluyendo el medicamento buscado.
        List<MedicamentosEntity> recomendaciones =
                medicamentosRepository.findByPrincipioActivoAndIdNot(buscado.getPrincipioActivo(), id);

        return toResponseList(recomendaciones);
    }
}
