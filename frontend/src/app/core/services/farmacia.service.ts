import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { FarmaciaMapa } from "../models/farmacia-mapa.model";
import { StompService } from "./stomp.service";

@Injectable({ providedIn: "root" })
export class FarmaciaService {
  private readonly stomp: StompService = inject(StompService);

  listarAprobadas(): Observable<FarmaciaMapa[]> {
    return this.stomp.consultar<FarmaciaMapa[]>("/app/farmacias/aprobadas");
  }
}
