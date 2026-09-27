import { ChangeDetectionStrategy, Component } from "@angular/core";

import { MapaComponent } from "../../shared/components/mapa/mapa.component";

@Component({
  selector: "app-landing",
  imports: [MapaComponent],
  templateUrl: "./landing.component.html",
  styleUrl: "./landing.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingComponent {}
