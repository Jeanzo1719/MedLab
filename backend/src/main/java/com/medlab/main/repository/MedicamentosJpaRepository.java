package com.medlab.main.repository;

import com.medlab.main.entity.MedicamentosEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // --- Reporte manual de la farmacia (HU-7) ---

    /**
     * Crea el medicamento solo si esa combinación no existe. El "id = id" no cambia nada,
     * solo evita el error por duplicado. No se usa INSERT IGNORE porque taparía también fallos
     * de llave foránea y de CHECK.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT INTO medicamentos
              (nombre_comercial, principio_activo, categoria_id, presentacion, forma_farmaceutica_id)
            VALUES (:nombre, :principio, :categoriaId, :presentacion, :formaId)
            ON DUPLICATE KEY UPDATE id = id
            """, nativeQuery = true)
    int crearSiNoExiste(@Param("nombre") String nombre, @Param("principio") String principio,
            @Param("categoriaId") Integer categoriaId, @Param("presentacion") String presentacion,
            @Param("formaId") Integer formaId);

    /** Id del medicamento con esa combinación única; vacío si no existe. */
    @Query(value = """
            SELECT id FROM medicamentos
            WHERE nombre_comercial = :nombre
              AND principio_activo = :principio
              AND presentacion = :presentacion
              AND forma_farmaceutica_id = :formaId
            """, nativeQuery = true)
    Optional<Long> buscarIdPorCombinacion(@Param("nombre") String nombre,
            @Param("principio") String principio, @Param("presentacion") String presentacion,
            @Param("formaId") Integer formaId);
}