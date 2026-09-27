package com.medlab.main.repository;

import com.medlab.main.entity.Medicamento;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Repository;

/** In-memory medicines with generic names, used until MongoDB is ready. */
@Repository
public class MedicamentoRepositoryMock implements MedicamentoRepository {

  private static final List<Medicamento> MEDICAMENTOS = List.of(
      new Medicamento("1", "Acetaminofén", "Acetaminofén", "Analgésico", "500 mg x 20", "Tableta"),
      new Medicamento("2", "Acetaminofén Jarabe", "Acetaminofén", "Analgésico", "150 mg/5 mL x 60 mL", "Jarabe"),
      new Medicamento("3", "Ibuprofeno", "Ibuprofeno", "Antiinflamatorio", "400 mg x 10", "Tableta"),
      new Medicamento("4", "Amoxicilina", "Amoxicilina", "Antibiótico", "500 mg x 21", "Cápsula"),
      new Medicamento("5", "Loratadina", "Loratadina", "Antihistamínico", "10 mg x 10", "Tableta"),
      new Medicamento("6", "Losartán", "Losartán potásico", "Antihipertensivo", "50 mg x 30", "Tableta"),
      new Medicamento("7", "Metformina", "Metformina clorhidrato", "Antidiabético", "850 mg x 30", "Tableta"),
      new Medicamento("8", "Omeprazol", "Omeprazol", "Antiulceroso", "20 mg x 14", "Cápsula"));

  @Override
  public List<Medicamento> findByNombreComercialContainingIgnoreCaseOrPrincipioActivoContainingIgnoreCase(
      String nombreComercial, String principioActivo) {
    return MEDICAMENTOS.stream()
        .filter(medicamento -> contiene(medicamento.getNombreComercial(), nombreComercial)
            || contiene(medicamento.getPrincipioActivo(), principioActivo))
        .toList();
  }
  //TODO: hay que conectarlo con la base de datos
  // 1. En pom.xml, añadir spring-boot-starter-data-mongodb (si no se hizo ya con FarmaciaRepository).
  // 2. Anotar Medicamento con @Document("medicamentos") e @Id en "id". Categoría y forma farmacéutica
  //    quedan embebidas como texto, así que los JOIN con categorias y formas_farmaceuticas desaparecen.
  // 3. Hacer que MedicamentoRepository extienda MongoRepository<Medicamento, String>: el método se deriva
  //    solo como una búsqueda por regex, sin distinguir mayúsculas.
  // 4. Configurar una collation "es" con strength 1 en la colección, para que "acetaminofen" encuentre
  //    "Acetaminofén" igual que hace este mock.
  // 5. Crear índices sobre nombreComercial y principioActivo (equivalen a idx_medicamentos_* del borrador
  //    SQL) y el índice único compuesto nombreComercial + principioActivo + presentacion + formaFarmaceutica.
  // 6. Borrar esta clase; MedicamentoService no cambia.

  /** Case- and accent-insensitive contains, mimicking a MongoDB collation of strength 1 */
  private static boolean contiene(String texto, String busqueda) {
    return normalizar(texto).contains(normalizar(busqueda));
  }

  private static String normalizar(String texto) {
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(Locale.ROOT);
  }
}
