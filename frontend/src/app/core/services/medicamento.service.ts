import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { API_RUTAS } from "../api/api-rutas";
import { MedicamentoBusqueda } from "../models/medicamento-busqueda.model";

@Injectable({ providedIn: "root" })
export class MedicamentoService {
  private readonly http: HttpClient = inject(HttpClient);

  buscar(texto: string): Observable<MedicamentoBusqueda[]> {
    const params: HttpParams = new HttpParams().set("q", texto);
    return this.http.get<MedicamentoBusqueda[]>(API_RUTAS.buscarMedicamentos, {
      params,
    });
  }
}
