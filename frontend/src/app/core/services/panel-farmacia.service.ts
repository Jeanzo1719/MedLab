import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { environment } from "../../../environments/environment";

/**
 * 
 * los tres estados de un producto en el inventario de la farmacia
 * 
 */
export type EstadoProducto = "disponible" | "agotado" | "bajo_pedido";

/**
 * 
 * cuántos productos hay en cada estado, para el resumen del panel. Es el espejo
 * de ResumenInventarioDto
 * 
 */
export interface ResumenInventario {
  porEstado: Record<EstadoProducto, number>;
  total: number;
}

/**
 * 
 * un producto del inventario tal como llega del backend. Es el espejo de
 * DisponibilidadFarmaciaDto. stockBajo ya viene calculado por el backend;
 * fechaHoraReporte viene en hora UTC, sin zona (usar formatearFechaColombia)
 * 
 */
export interface ProductoInventario {
  medicamentoId: number;
  nombreComercial: string;
  principioActivo: string;
  presentacion: string;
  cantidadDisponible: number;
  precio: number;
  estado: EstadoProducto;
  stockBajo: boolean;
  fechaHoraReporte: string;
}

/**
 * 
 * una página de resultados. Es el espejo de PaginaDto; la primera página es la 0
 * 
 */
export interface Pagina<T> {
  contenido: T[];
  pagina: number;
  tamano: number;
  totalElementos: number;
  totalPaginas: number;
}

/**
 * 
 * lo que se envía para crear o editar un producto. Si no se manda estado, el backend
 * lo deduce de la cantidad (0 es agotado, más de 0 es disponible)
 * 
 */
export interface GuardarProducto {
  medicamentoId: number;
  cantidad: number;
  precio: number;
  estado?: EstadoProducto | null;
}

/**
 * 
 * una opción de un selector (categoría o forma farmacéutica). Es el espejo de
 * CatalogosDto.Opcion
 * 
 */
export interface OpcionCatalogo {
  id: number;
  nombre: string;
}

/**
 * 
 * las opciones del formulario de reporte manual (HU-7). Es el espejo de CatalogosDto
 * 
 */
export interface Catalogos {
  categorias: OpcionCatalogo[];
  formasFarmaceuticas: OpcionCatalogo[];
  estados: EstadoProducto[];
}

/**
 * 
 * lo que se envía al reportar un medicamento a mano (HU-7): los datos del medicamento
 * más cantidad, precio y estado. Es el espejo de GuardarDisponibilidadManualDto. Si el
 * medicamento ya existe con esa combinación, el backend lo reutiliza en vez de crear otro
 * 
 */
export interface GuardarProductoManual {
  nombreComercial: string;
  principioActivo: string;
  categoriaId: number;
  presentacion: string;
  formaFarmaceuticaId: number;
  cantidad: number;
  precio: number;
  estado?: EstadoProducto | null;
}

/**
 * 
 * los datos de la farmacia tal como los muestra el panel. Es el espejo de
 * DatosFarmaciaDto; direccion ya viene armada para mostrarla tal cual
 * 
 */
export interface DatosFarmacia {
  id: number;
  nombreFarmacia: string;
  tipoViaPrincipal: string;
  numeroViaPrincipal: string;
  letraViaPrincipal: string | null;
  numeroViaGeneradora: string;
  letraViaGeneradora: string | null;
  numeroPredio: string;
  complemento: string | null;
  direccion: string;
  horarioAtencion: string | null;
  latitud: number | null;
  longitud: number | null;
  estado: string;
  fechaUltimaActualizacion: string;
}

/**
 * 
 * lo que la farmacia puede editar de sus datos. Latitud y longitud van juntas o
 * ninguna: si no se envían, el marcador del mapa se queda donde estaba
 * 
 */
export interface GuardarDatosFarmacia {
  nombreFarmacia: string;
  tipoViaPrincipal: string;
  numeroViaPrincipal: string;
  letraViaPrincipal?: string | null;
  numeroViaGeneradora: string;
  letraViaGeneradora?: string | null;
  numeroPredio: string;
  complemento?: string | null;
  horarioAtencion?: string | null;
  latitud?: number | null;
  longitud?: number | null;
}

/**
 * 
 * el service del panel de la farmacia. Habla con la API REST del backend
 * (/api/panel-farmacia); el resto de la app usa WebSocket, pero el panel escribe
 * datos de una sola farmacia y por eso es REST
 * 
 */
@Injectable({ providedIn: "root" })
export class PanelFarmaciaService {
  private readonly http: HttpClient = inject(HttpClient);
  private readonly base: string = `${environment.apiUrl}/panel-farmacia`;

  obtenerResumen(): Observable<ResumenInventario> {
    return this.http.get<ResumenInventario>(`${this.base}/resumen`, { headers: this.encabezados() });
  }

  /**
   * 
   * pide una página del inventario, de la A a la Z. Con texto vacío trae todo; con
   * texto, solo los productos cuyo nombre comercial lo contiene
   * 
   */
  listarInventario(texto: string, pagina: number, tamano: number): Observable<Pagina<ProductoInventario>> {
    const params: HttpParams = new HttpParams()
      .set("texto", texto)
      .set("pagina", pagina)
      .set("tamano", tamano);
    return this.http.get<Pagina<ProductoInventario>>(`${this.base}/inventario`, {
      headers: this.encabezados(),
      params,
    });
  }

  /**
   * 
   * crea el producto o, si la farmacia ya lo tenía, lo actualiza
   * 
   */
  guardarProducto(datos: GuardarProducto): Observable<ProductoInventario> {
    return this.http.put<ProductoInventario>(`${this.base}/inventario`, datos, {
      headers: this.encabezados(),
    });
  }

    /**
   * 
   * pide las opciones de los selectores del formulario de reporte manual
   * 
   */
  obtenerCatalogos(): Observable<Catalogos> {
    return this.http.get<Catalogos>(`${this.base}/catalogos`, { headers: this.encabezados() });
  }

  /**
   * 
   * reporta un medicamento por sus datos: si no estaba en el catálogo lo crea, y guarda
   * su disponibilidad en la farmacia (o la actualiza si ya la tenía)
   * 
   */
  guardarProductoManual(datos: GuardarProductoManual): Observable<ProductoInventario> {
    return this.http.put<ProductoInventario>(`${this.base}/inventario/manual`, datos, {
      headers: this.encabezados(),
    });
  }

  /**
   * 
   * da de baja un producto: queda en cero y agotado, pero no se borra
   * 
   */
  marcarAgotado(medicamentoId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/inventario/${medicamentoId}`, {
      headers: this.encabezados(),
    });
  }

  obtenerDatos(): Observable<DatosFarmacia> {
    return this.http.get<DatosFarmacia>(`${this.base}/datos`, { headers: this.encabezados() });
  }

  guardarDatos(datos: GuardarDatosFarmacia): Observable<DatosFarmacia> {
    return this.http.put<DatosFarmacia>(`${this.base}/datos`, datos, { headers: this.encabezados() });
  }

  /**
   * 
   * TEMPORAL: dice al backend qué farmacia es. Es el único lugar que lo hace; cuando
   * exista el login (HU-2) se cambia por el token y nada más se toca
   * 
   */
  private encabezados(): HttpHeaders {
    return new HttpHeaders({ "X-Farmacia-Id": String(environment.farmaciaIdTemporal) });
  }
}

/**
 * 
 * saca el mensaje legible de un error de la API. El backend responde {"mensaje": "..."};
 * si no hay respuesta (backend apagado) o no trae mensaje, devuelve un texto genérico
 * 
 */
export function mensajeDeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return "No hay conexión con el servidor";
    }
    const mensaje: unknown = error.error?.mensaje;
    if (typeof mensaje === "string" && mensaje.length > 0) {
      return mensaje;
    }
  }
  return "Ocurrió un error inesperado";
}

/**
 * 
 * convierte una fecha del backend a hora de Colombia. El backend guarda y envía
 * las fechas en UTC sin zona; por eso se le agrega la Z antes de convertirla
 * 
 */
export function formatearFechaColombia(fecha: string): string {
  const iso: string = fecha.endsWith("Z") ? fecha : `${fecha}Z`;
  return new Intl.DateTimeFormat("es-CO", {
    timeZone: "America/Bogota",
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(iso));
}