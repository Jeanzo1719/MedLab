package com.medlab.main.repository;

import com.medlab.main.entity.MedicamentosEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentosJpaRepository extends JpaRepository<MedicamentosEntity, Long> {

    Optional<MedicamentosEntity> findByNombreComercial(String nombreComercial);

    Optional<MedicamentosEntity> findByPrincipioActivo(String principioActivo);

    List<MedicamentosEntity> findByPrincipioActivoAndIdNot(String principioActivo, Long id);

    /** Trae el medicamento con su categoria y su formaFarmaceutica en una sola consulta. */
    @Override
    @EntityGraph(attributePaths = {"categoria", "formaFarmaceutica"})
    Optional<MedicamentosEntity> findById(Long id);

    /** Busca por nombre comercial o principio activo, sin distinguir mayusculas, trayendo la
     * formaFarmaceutica de una sola vez. Basta con que coincida uno de los dos campos. */
    @EntityGraph(attributePaths = {"formaFarmaceutica"})
    List<MedicamentosEntity> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
            String nombreComercial, String principioActivo);
}
