import { Injectable, inject } from "@angular/core";
import { EMPTY, Observable, catchError, merge } from "rxjs";

import { StompService } from "./stomp.service";

/**
 * 
 * la forma de cada resultado del buscador tal como llega del backend. Es el
 * espejo de MedicamentoBusquedaDto: mismos campos y mismos nombres
 * 
 * no guarda datos ni hace nada, solo le dice a TypeScript qué esperar
 * 
 */
export interface MedicamentoBusqueda {
  /**
   * 
   * el número que identifica al medicamento
   * 
   */
  id: number;

  /**
   * 
   * el nombre de marca con el que se vende, por ejemplo "Dolex"
   * 
   */
  nombreComercial: string;

  /**
   * 
   * la sustancia del medicamento que hace efecto, por ejemplo "Acetaminofén"
   * 
   */
  principioActivo: string;

  /**
   * 
   * cómo viene empacado, por ejemplo "500 mg x 10"
   * 
   */
  presentacion: string;

  /**
   * 
   * la forma en que viene, por ejemplo "Tableta"
   * 
   */
  formaFarmaceutica: string;
}

/**
 * 
 * un reporte de disponibilidad de una farmacia, con lo que dicen las
 * calificaciones de los usuarios. Trae solo los campos de
 * DisponibilidadReporteDto que se usan
 * 
 */
export interface ReporteDisponibilidad {
  /**
   * 
   * el número que identifica al reporte
   * 
   */
  disponibilidadId: number;

  /**
   * 
   * el mensaje que se muestra, por ejemplo "Según los últimos reportes, el
   * medicamento está disponible"
   * 
   */
  mensajeDisponibilidad: string;
}

/**
 * 
 * el detalle de un medicamento con los reportes de cada farmacia. Trae solo los
 * campos de MedicamentoDetalleDto que se usan
 * 
 */
export interface MedicamentoDetalle {
  /**
   * 
   * el número que identifica al medicamento
   * 
   */
  id: number;

  /**
   * 
   * un reporte por cada farmacia que tiene el medicamento
   * 
   */
  reportes: ReporteDisponibilidad[];
}

/**
 * 
 * el service de los medicamentos en el frontend. Le pide al backend los
 * resultados del buscador
 * 
 */
@Injectable({ providedIn: "root" })
export class MedicamentoService {
  /**
   * 
   * la conexión WebSocket con el backend
   * 
   */
  private readonly stomp: StompService = inject(StompService);

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
}
