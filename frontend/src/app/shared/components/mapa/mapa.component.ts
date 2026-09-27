import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  OnDestroy,
  effect,
  input,
  signal,
  viewChild,
} from "@angular/core";
import {
  Map as LeafletMap,
  circleMarker,
  layerGroup,
  map,
  tileLayer,
} from "leaflet";

import {
  CAPA_BASE,
  CENTRO_INICIAL,
  ESTILO_MARCADOR,
  LIMITES_MUNDO,
  ZOOM_INICIAL,
  ZOOM_MAXIMO,
  ZOOM_MINIMO,
} from "./mapa.config";
import { MarcadorMapa } from "./marcador-mapa.model";

@Component({
  selector: "app-mapa",
  templateUrl: "./mapa.component.html",
  styleUrl: "./mapa.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapaComponent implements AfterViewInit, OnDestroy {
  readonly marcadores = input<MarcadorMapa[]>([]);

  private readonly contenedor =
    viewChild.required<ElementRef<HTMLDivElement>>("contenedorMapa");

  // A signal so the markers effect re-runs once the map exists
  private readonly mapa = signal<LeafletMap | undefined>(undefined);
  private readonly capaMarcadores = layerGroup();

  constructor() {
    // Redraws every marker whenever the input changes
    effect(() => {
      if (!this.mapa()) {
        return;
      }

      this.capaMarcadores.clearLayers();
      for (const marcador of this.marcadores()) {
        circleMarker([marcador.latitud, marcador.longitud], ESTILO_MARCADOR)
          .bindTooltip(marcador.etiqueta)
          .addTo(this.capaMarcadores);
      }
    });
  }

  // Leaflet needs the container already in the DOM, so it is created here
  ngAfterViewInit(): void {
    const mapa = map(this.contenedor().nativeElement, {
      center: CENTRO_INICIAL,
      zoom: ZOOM_INICIAL,
      minZoom: ZOOM_MINIMO,
      maxZoom: ZOOM_MAXIMO,
      maxBounds: LIMITES_MUNDO,
      maxBoundsViscosity: 1,
    });

    tileLayer(CAPA_BASE.url, {
      attribution: CAPA_BASE.atribucion,
      maxZoom: ZOOM_MAXIMO,
      noWrap: true,
    }).addTo(mapa);

    this.capaMarcadores.addTo(mapa);
    this.mapa.set(mapa);
  }

  // Releases Leaflet listeners and layers when the view is destroyed
  ngOnDestroy(): void {
    this.mapa()?.remove();
  }
}
