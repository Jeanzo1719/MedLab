import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { API_RUTAS } from "../api/api-rutas";
import { FarmaciaMapa } from "../models/farmacia-mapa.model";

@Injectable({ providedIn: "root" })
export class FarmaciaService {
  private readonly http: HttpClient = inject(HttpClient);

  /** Public endpoint: only approved pharmacies, no authentication needed */
  listarAprobadas(): Observable<FarmaciaMapa[]> {
    return this.http.get<FarmaciaMapa[]>(API_RUTAS.farmaciasAprobadas);
  }
}
