import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { EMPTY, Observable, catchError, map, merge } from "rxjs";

import { environment } from "../../../environments/environment";
import { StompService } from "./stomp.service";

/**
 *
 * la forma de cada resultado del buscador tal como llega del backend. Es el
 * espejo de MedicamentoBusquedaDto: mismos campos y mismos nombres
 *
 */
export interface MedicamentoBusqueda {
  /** el número que identifica al medicamento */
  id: number;
  /** el nombre de marca con el que se vende, por ejemplo "Dolex" */
  nombreComercial: string;
  /** la sustancia del medicamento que hace efecto, por ejemplo "Acetaminofén" */
  principioActivo: string;
  /** cómo viene empacado, por ejemplo "500 mg x 10" */
  presentacion: string;
  /** la forma en que viene, por ejemplo "Tableta" */
  formaFarmaceutica: string;
  /** el nombre de la categoría o uso general, por ejemplo "Analgésico" */
  categoria: string;
  /** el precio más bajo entre las farmacias aprobadas que lo reportan */
  precioMinimo: number | null;
  /** cuántas farmacias aprobadas lo tienen en estado disponible */
  farmaciasDisponibles: number;
  /** cuántas farmacias aprobadas lo han reportado */
  farmaciasReportaron: number;
}

/**
 *
 * los filtros que el usuario armó en el buscador. Se envían como encabezados de
 * texto al backend, igual que espera BusquedaMedicamentosDto
 *
 */
export interface FiltrosMedicamentos {
  /** lo escrito en el campo de búsqueda */
  texto: string;
  /** en qué campo se busca: nombre, principio o todos */
  campo: "nombre" | "principio" | "todos";
  /** nombre o parte del nombre de una farmacia aprobada */
  farmacia: string;
  /** precio mínimo del rango, o null si no se limita */
  precioMin: number | null;
  /** precio máximo del rango, o null si no se limita */
  precioMax: number | null;
  /** relevancia (por coincidencia) o precio (de menor a mayor) */
  orden: "relevancia" | "precio";
  /** página pedida, desde 0 */
  pagina: number;
  /** resultados por página */
  tamano: number;
}

/**
 *
 * una página de resultados del buscador. Es el espejo de PaginaMedicamentosDto
 *
 */
export interface PaginaMedicamentos {
  resultados: MedicamentoBusqueda[];
  total: number;
  pagina: number;
  tamano: number;
  totalPaginas: number;
  hayMas: boolean;
}

/**
 *
 * un reporte de disponibilidad de una farmacia, con lo que dicen las
 * calificaciones de los usuarios. Es el espejo de DisponibilidadReporteDto
 *
 */
export interface ReporteDisponibilidad {
  /** el número que identifica al reporte */
  disponibilidadId: number;
  /** el nombre de la farmacia que lo reportó */
  farmacia: string;
  /** la fecha y hora del último reporte */
  fechaHoraReporte: string;
  /** la misma fecha en texto relativo, por ejemplo "hace 3 horas" */
  reporteRelativo: string;
  /** DISPONIBLE, BAJO_PEDIDO, NO_DISPONIBLE, SIN_CONSENSO o SIN_CALIFICACIONES */
  tendencia: string;
  /** el mensaje de la tendencia listo para mostrar */
  mensajeDisponibilidad: string;
  /** el precio reportado por la farmacia */
  precio: number;
  /** las unidades que la farmacia dice tener */
  cantidadDisponible: number;
  /** disponible, agotado o bajo_pedido; null si el reporte no lo trae */
  estado: string | null;
  /** porcentaje (0 a 100) de calificaciones recientes que apoyan la tendencia */
  confianza: number;
}

/**
 *
 * el detalle de un medicamento con los reportes de cada farmacia. Es el espejo
 * de MedicamentoDetalleDto
 *
 */
export interface MedicamentoDetalle {
  /** el número que identifica al medicamento */
  id: number;
  /** el nombre de marca */
  nombreComercial: string;
  /** la sustancia que hace efecto */
  principioActivo: string;
  /** el nombre de la categoría o uso general */
  categoria: string;
  /** cómo viene empacado */
  presentacion: string;
  /** la forma en que viene, por ejemplo "Tableta" */
  formaFarmaceutica: string;
  /** un reporte por cada farmacia que tiene el medicamento */
  reportes: ReporteDisponibilidad[];
}

/**
 *
 * un medicamento recomendado: otro que comparte el principio activo. Es el
 * espejo de MedicamentoResponseDto
 *
 */
export interface MedicamentoRecomendado {
  id: number;
  nombreComercial: string;
  principioActivo: string;
  categoriaId: number | null;
  categoriaNombre: string | null;
  presentacion: string;
  formaFarmaceuticaId: number | null;
  formaFarmaceuticaNombre: string | null;
}

/** La envoltura estándar de las respuestas REST del backend. */
interface RespuestaApi<T> {
  code: number;
  message: string;
  data: T;
}

/**
 *
 * el service de los medicamentos en el frontend. Le pide al backend los
 * resultados del buscador, el detalle y las recomendaciones
 *
 */
@Injectable({ providedIn: "root" })
export class MedicamentoService {
  private readonly stomp: StompService = inject(StompService);
  private readonly http: HttpClient = inject(HttpClient);

  /**
   *
   * devuelve los medicamentos cuyo nombre comercial o principio activo contiene
   * el texto que escribió el usuario
   *
   * se lo pide al backend por WebSocket en el destino /app/medicamentos/buscar,
   * enviando el texto como un dato extra llamado q
   *
   */
  buscar(texto: string): Observable<MedicamentoBusqueda[]> {
    return this.stomp.consultar<MedicamentoBusqueda[]>("/app/medicamentos/buscar", { q: texto });
  }

  /**
   *
   * devuelve una página de resultados aplicando todos los filtros del buscador
   * del paciente: texto, campo, farmacia, rango de precio, orden y página
   *
   * se lo pide al backend por WebSocket en /app/medicamentos/buscar/filtros. Los
   * encabezados viajan como texto porque así los entrega STOMP; los valores
   * vacíos se omiten para que el backend use sus valores por defecto
   *
   */
  buscarConFiltros(filtros: FiltrosMedicamentos): Observable<PaginaMedicamentos> {
    const encabezados: Record<string, string> = {
      campo: filtros.campo,
      orden: filtros.orden,
      pagina: String(filtros.pagina),
      tamano: String(filtros.tamano),
    };
    if (filtros.texto.trim()) {
      encabezados["q"] = filtros.texto.trim();
    }
    if (filtros.farmacia.trim()) {
      encabezados["farmacia"] = filtros.farmacia.trim();
    }
    if (filtros.precioMin !== null) {
      encabezados["precioMin"] = String(filtros.precioMin);
    }
    if (filtros.precioMax !== null) {
      encabezados["precioMax"] = String(filtros.precioMax);
    }

    return this.stomp.consultar<PaginaMedicamentos>(
      "/app/medicamentos/buscar/filtros",
      encabezados,
    );
  }

  /**
   *
   * devuelve el detalle del medicamento y cada actualización que llegue
   *
   * primero lo pide al backend en /app/medicamentos/detalle (si esa respuesta
   * falla, solo se ignora) y a la vez escucha /topic/medicamentos/{id}, donde el
   * backend lo vuelve a enviar cada vez que alguien califica un reporte
   *
   */
  seguirDetalle(id: number): Observable<MedicamentoDetalle> {
    return merge(
      this.stomp
        .consultar<MedicamentoDetalle>("/app/medicamentos/detalle", { medicamentoId: String(id) })
        .pipe(catchError(() => EMPTY)),
      this.stomp.escuchar<MedicamentoDetalle>(`/topic/medicamentos/${id}`),
    );
  }

  /** Devuelve otros medicamentos que comparten el principio activo del elegido. */
  obtenerRecomendaciones(id: number): Observable<MedicamentoRecomendado[]> {
    return this.http
      .get<RespuestaApi<MedicamentoRecomendado[]>>(
        `${environment.apiUrl}/medicamentos/${id}/recomendaciones`,
      )
      .pipe(map((respuesta: RespuestaApi<MedicamentoRecomendado[]>): MedicamentoRecomendado[] => respuesta.data));
  }

  /**
   * Guarda la calificación del usuario sobre un reporte. El backend toma el
   * usuario del token de la sesión.
   */
  calificar(disponibilidadId: number, tipoCalificacionId: number): Observable<void> {
    return this.http
      .post<RespuestaApi<void>>(`${environment.apiUrl}/calificaciones`, {
        disponibilidadId,
        tipoCalificacionId,
      })
      .pipe(map((): void => undefined));
  }

  /** Convierte un error HTTP en el texto que se le muestra al usuario. */
  mensajeDeError(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return "No hay conexión con el servidor";
      }
      if (error.status === 401 || error.status === 403) {
        return "Tu sesión no permite calificar. Vuelve a iniciar sesión.";
      }
      if (error.status === 404) {
        return "El reporte ya no existe";
      }
      return "No se pudo guardar la calificación";
    }
    return "No se pudo guardar la calificación";
  }
}
