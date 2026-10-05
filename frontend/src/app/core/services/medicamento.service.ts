import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

import { MedicamentoBusqueda } from "../models/medicamento-busqueda.model";
import { StompService } from "./stomp.service";

@Injectable({ providedIn: "root" })
export class MedicamentoService {
  private readonly stomp: StompService = inject(StompService);

  buscar(texto: string): Observable<MedicamentoBusqueda[]> {
    return this.stomp.consultar<MedicamentoBusqueda[]>("/app/medicamentos/buscar", { q: texto });
  }
}
