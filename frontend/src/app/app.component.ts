import { Component, inject } from "@angular/core";
import { RouterOutlet } from "@angular/router";

import { MetadatosService } from "./core/services/metadatos.service";

@Component({
  selector: "app-root",
  imports: [RouterOutlet],
  templateUrl: "./app.component.html",
})
export class AppComponent {
  constructor() {
    inject(MetadatosService).aplicarGenerales();
  }
}
