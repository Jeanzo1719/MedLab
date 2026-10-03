import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  InputSignal,
  OnDestroy,
  Signal,
  WritableSignal,
  effect,
  input,
  signal,
  viewChild,
} from "@angular/core";
import {
  LayerGroup,
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
  readonly marcadores: InputSignal<MarcadorMapa[]> = input<MarcadorMapa[]>([]);
  readonly descripcion: InputSignal<string> = input<string>("Mapa interactivo");

  private readonly contenedor: Signal<ElementRef<HTMLDivElement>> =
    viewChild.required<ElementRef<HTMLDivElement>>("contenedorMapa");

  private readonly mapa: WritableSignal<LeafletMap | undefined> =
    signal<LeafletMap | undefined>(undefined);
  private readonly capaMarcadores: LayerGroup = layerGroup();

  constructor() {
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

  ngAfterViewInit(): void {
    const mapa: LeafletMap = map(this.contenedor().nativeElement, {
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

  ngOnDestroy(): void {
    this.mapa()?.remove();
  }
}
