package com.medlab.main.controller;

import com.medlab.main.dto.BusquedaMedicamentosDto;
import com.medlab.main.dto.MedicamentoBusquedaDto;
import com.medlab.main.dto.PaginaMedicamentosDto;
import com.medlab.main.service.MedicamentosService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Atiende las búsquedas de medicamentos por WebSocket: la búsqueda rápida de la landing y la
 * búsqueda combinada con filtros y paginación del panel del paciente/cuidador (HU-3).
 */
@Controller
@Validated
@AllArgsConstructor
public class MedicamentosBusquedaController {

  private final MedicamentosService medicamentosService;

  /**
   * Recibe el texto que escribió el usuario y responde con la lista de medicamentos que
   * coinciden, una sola vez y solo a quien se suscribió a /app/medicamentos/buscar.
   *
   * El texto llega como un dato extra de la suscripción llamado q (de query, "consulta").
   * {@code @NotBlank} y {@code @Size} revisan que no esté vacío y que tenga entre 2 y 150
   * caracteres; si no cumple, no se busca nada y el error queda en el log del backend. Si es
   * válido, se lo pasa al service.
   */
  @SubscribeMapping("/medicamentos/buscar")
  public List<MedicamentoBusquedaDto> searchMedicamentos(
      @Header("q") @NotBlank @Size(min = 2, max = 150) String textoBusqueda) {
    return medicamentosService.searchMedicamentosByTexto(textoBusqueda);
  }

  /**
   * Búsqueda combinada de la página del paciente/cuidador (HU-3).
   *
   * Todos los datos llegan como encabezados opcionales de la suscripción STOMP:
   * q (texto), campo (nombre, principio o todos), farmacia, precioMin, precioMax, orden
   * (relevancia o precio), pagina (desde 0) y tamano (1 a 50). Se reciben como texto y
   * BusquedaMedicamentosDto los normaliza: los valores que no se entienden se descartan en
   * vez de lanzar una excepción, para que el cliente siempre reciba una página.
   *
   * Devuelve una página vacía si no hay criterios o si ninguna coincidencia existe; así el
   * frontend puede mostrar "sin resultados" sin tratar un error como si fuera una falla del
   * servidor.
   */
  @SubscribeMapping("/medicamentos/buscar/filtros")
  public PaginaMedicamentosDto buscarMedicamentos(
      @Header(value = "q", required = false) String texto,
      @Header(value = "campo", required = false) String campo,
      @Header(value = "farmacia", required = false) String farmacia,
      @Header(value = "precioMin", required = false) String precioMin,
      @Header(value = "precioMax", required = false) String precioMax,
      @Header(value = "orden", required = false) String orden,
      @Header(value = "pagina", required = false) String pagina,
      @Header(value = "tamano", required = false) String tamano) {
    return medicamentosService.buscarMedicamentos(
        BusquedaMedicamentosDto.desde(texto, campo, farmacia, precioMin, precioMax, orden, pagina,
            tamano));
  }
}
