import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { environment } from "../../../environments/environment";
import { FarmaciaMapa } from "../models/farmacia-mapa.model";

@Injectable({ providedIn: "root" })
export class FarmaciaService {
  private readonly http: HttpClient = inject(HttpClient);

  listarAprobadas(): Observable<FarmaciaMapa[]> {
    return this.http.get<FarmaciaMapa[]>(`${environment.apiUrl}/public/farmacias/aprobadas`);
  }
}
