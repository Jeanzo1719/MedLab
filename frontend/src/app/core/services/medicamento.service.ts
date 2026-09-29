import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { API_RUTAS } from "../api/api-rutas";
import { MedicamentoBusqueda } from "../models/medicamento-busqueda.model";

/**
 * Servicio de acceso a los datos de medicamentos (capa core, servicios).
 *
 * Qué es: el único punto del frontend que busca medicamentos en el backend.
 *
 * Cómo funciona: hace un GET con HttpClient a API_RUTAS.buscarMedicamentos con
 * el parámetro q y devuelve un Observable tipado con el modelo
 * MedicamentoBusqueda.
 *
 * Para qué sirve: aísla al buscador de la landing del HTTP; el componente solo
 * maneja los estados de la búsqueda.
 */
@Injectable({ providedIn: "root" })
export class MedicamentoService {
  private readonly http: HttpClient = inject(HttpClient);

  /** Endpoint público: busca por nombre comercial o principio activo, sin necesidad de cuenta */
  buscar(texto: string): Observable<MedicamentoBusqueda[]> {
    // HttpParams codifica el texto, así las tildes y los espacios llegan intactos a la API
    const params: HttpParams = new HttpParams().set("q", texto);
    return this.http.get<MedicamentoBusqueda[]>(API_RUTAS.buscarMedicamentos, {
      params,
    });
  }
}
