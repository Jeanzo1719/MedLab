import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { environment } from "../../../environments/environment";
import { MedicamentoBusqueda } from "../models/medicamento-busqueda.model";

@Injectable({ providedIn: "root" })
export class MedicamentoService {
  private readonly http: HttpClient = inject(HttpClient);
  private readonly url: string = `${environment.apiUrl}/public/medicamentos/buscar`;

  /** Public endpoint: search by commercial name or active ingredient, no account needed */
  buscar(texto: string): Observable<MedicamentoBusqueda[]> {
    // HttpParams encodes the text, so accents and spaces reach the API intact
    const params: HttpParams = new HttpParams().set("q", texto);
    return this.http.get<MedicamentoBusqueda[]>(this.url, { params });
  }
}
