import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { API_RUTAS } from "../api/api-rutas";
import { FarmaciaMapa } from "../models/farmacia-mapa.model";

/**
 * Servicio de acceso a los datos de farmacias (capa core, servicios).
 *
 * Qué es: el único punto del frontend que pide farmacias al backend.
 *
 * Cómo funciona: hace un GET con HttpClient a API_RUTAS.farmaciasAprobadas y
 * devuelve un Observable tipado con el modelo FarmaciaMapa. No maneja errores ni
 * estado: eso lo decide quien lo usa (por ejemplo, LandingComponent).
 *
 * Para qué sirve: aísla a los componentes del HTTP, así una página nunca arma
 * URL ni conoce detalles de la API.
 */
@Injectable({ providedIn: "root" })
export class FarmaciaService {
  private readonly http: HttpClient = inject(HttpClient);

  /** Endpoint público: solo farmacias aprobadas, sin autenticación */
  listarAprobadas(): Observable<FarmaciaMapa[]> {
    return this.http.get<FarmaciaMapa[]>(API_RUTAS.farmaciasAprobadas);
  }
}
