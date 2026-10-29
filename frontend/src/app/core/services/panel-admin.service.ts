import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { environment } from "../../../environments/environment";
import { Pagina } from "./panel-farmacia.service";
import { RolUsuario } from "./sesion.service";

/**
 * 
 * los tres estados en que puede estar una farmacia
 * 
 */
export type EstadoFarmacia = "pendiente" | "aprobada" | "suspendida";

/**
 * 
 * una fila del historial de usuarios. Es el espejo de UsuarioHistorialDto; el nombre
 * es null para el administrador, que no tiene perfil
 * 
 */
export interface UsuarioHistorial {
  id: number;
  identificacion: string;
  rol: RolUsuario;
  nombre: string | null;
  fechaRegistro: string;
}

/**
 * 
 * una fila del historial de farmacias. Es el espejo de FarmaciaHistorialDto
 * 
 */
export interface FarmaciaHistorial {
  id: number;
  nombre: string;
  estado: EstadoFarmacia;
  fechaUltimaActualizacion: string;
  alertasPendientes: number;
}

/**
 * 
 * una farmacia con alertas de inactividad. Es el espejo de FarmaciaConAlertasDto
 * 
 */
export interface FarmaciaConAlertas {
  id: number;
  nombre: string;
  estado: EstadoFarmacia;
  totalAlertas: number;
  alertasPendientes: number;
  ultimaAlerta: string | null;
}

/**
 * 
 * un cambio de estado de una farmacia. Es el espejo de AuditoriaEstadoDto; automatico
 * es true cuando lo hizo el sistema (entonces usuarioAdminId es null)
 * 
 */
export interface AuditoriaEstado {
  id: number;
  estadoAnterior: string;
  estadoNuevo: string;
  usuarioAdminId: number | null;
  automatico: boolean;
  fecha: string;
}

/**
 * 
 * una publicación de una farmacia vista por el administrador. Es el espejo de
 * DisponibilidadAdminDto
 * 
 */
export interface DisponibilidadAdmin {
  id: number;
  medicamento: string;
  presentacion: string;
  estado: string;
  cantidad: number;
  totalCalificaciones: number;
  fechaHoraReporte: string;
}

/**
 * 
 * una calificación de una publicación. Es el espejo de CalificacionAdminDto
 * 
 */
export interface CalificacionAdmin {
  id: number;
  usuarioId: number;
  identificacionUsuario: string;
  tipo: string;
  fechaHora: string;
}

/**
 * 
 * el resultado de lanzar la revisión de inactividad a mano. Es el espejo de
 * RevisionInactividadDto
 * 
 */
export interface RevisionInactividad {
  alertasGeneradas: number;
  farmaciasSuspendidas: number;
}

/**
 * 
 * una publicación con muchas calificaciones "No hay". Es el espejo de
 * DisponibilidadSospechosaDto y también lo que llega en vivo por /topic/admin/calificaciones
 * 
 */
export interface DisponibilidadSospechosa {
  disponibilidadId: number;
  farmaciaId: number;
  farmacia: string;
  medicamento: string;
  presentacion: string;
  calificacionesNoHay: number;
  calificacionesRevisadas: number;
}

/**
 * 
 * el service del panel del administrador. Habla con la API REST del backend
 * (/api/panel-admin), igual que el panel de la farmacia
 * 
 */
@Injectable({ providedIn: "root" })
export class PanelAdminService {
  private readonly http: HttpClient = inject(HttpClient);
  private readonly base: string = `${environment.apiUrl}/panel-admin`;

  /**
   * 
   * rol vacío trae todos los usuarios
   * 
   */
  listarUsuarios(rol: RolUsuario | "", pagina: number, tamano: number): Observable<Pagina<UsuarioHistorial>> {
    return this.http.get<Pagina<UsuarioHistorial>>(`${this.base}/usuarios`, {
      headers: this.encabezados(),
      params: this.paginacion(pagina, tamano).set("rol", rol),
    });
  }

  /**
   * 
   * estado vacío trae todas las farmacias
   * 
   */
  listarFarmacias(estado: EstadoFarmacia | "", pagina: number, tamano: number): Observable<Pagina<FarmaciaHistorial>> {
    return this.http.get<Pagina<FarmaciaHistorial>>(`${this.base}/farmacias`, {
      headers: this.encabezados(),
      params: this.paginacion(pagina, tamano).set("estado", estado),
    });
  }

  listarFarmaciasConAlertas(pagina: number, tamano: number): Observable<Pagina<FarmaciaConAlertas>> {
    return this.http.get<Pagina<FarmaciaConAlertas>>(`${this.base}/farmacias/alertas`, {
      headers: this.encabezados(),
      params: this.paginacion(pagina, tamano),
    });
  }

  listarAuditoria(farmaciaId: number, pagina: number, tamano: number): Observable<Pagina<AuditoriaEstado>> {
    return this.http.get<Pagina<AuditoriaEstado>>(`${this.base}/farmacias/${farmaciaId}/auditoria`, {
      headers: this.encabezados(),
      params: this.paginacion(pagina, tamano),
    });
  }

  listarDisponibilidades(farmaciaId: number, pagina: number, tamano: number): Observable<Pagina<DisponibilidadAdmin>> {
    return this.http.get<Pagina<DisponibilidadAdmin>>(`${this.base}/farmacias/${farmaciaId}/disponibilidades`, {
      headers: this.encabezados(),
      params: this.paginacion(pagina, tamano),
    });
  }

  listarCalificaciones(disponibilidadId: number, pagina: number, tamano: number): Observable<Pagina<CalificacionAdmin>> {
    return this.http.get<Pagina<CalificacionAdmin>>(`${this.base}/disponibilidades/${disponibilidadId}/calificaciones`, {
      headers: this.encabezados(),
      params: this.paginacion(pagina, tamano),
    });
  }

  /**
   * 
   * elimina una calificación falsa; la publicación a la que pertenece no se toca
   * 
   */
  eliminarCalificacion(calificacionId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/calificaciones/${calificacionId}`, {
      headers: this.encabezados(),
    });
  }

  /**
   * 
   * lanza a mano la misma revisión que hace el job diario de las 3:00 a.m.
   * 
   */
  revisarInactividad(): Observable<RevisionInactividad> {
    return this.http.post<RevisionInactividad>(`${this.base}/inactividad/revisar`, null, {
      headers: this.encabezados(),
    });
  }

  listarSospechosas(): Observable<DisponibilidadSospechosa[]> {
    return this.http.get<DisponibilidadSospechosa[]>(`${this.base}/disponibilidades/sospechosas`, {
      headers: this.encabezados(),
    });
  }

  private paginacion(pagina: number, tamano: number): HttpParams {
    return new HttpParams().set("pagina", pagina).set("tamano", tamano);
  }

  /**
   * 
   * TEMPORAL: dice al backend qué administrador es. Es el único lugar que lo hace; cuando
   * exista el login (HU-2) se cambia por el token y nada más se toca
   * 
   */
  private encabezados(): HttpHeaders {
    return new HttpHeaders({ "X-Admin-Id": String(environment.adminIdTemporal) });
  }
}