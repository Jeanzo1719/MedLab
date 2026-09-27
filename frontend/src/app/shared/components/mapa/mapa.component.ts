import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  OnDestroy,
  viewChild,
} from "@angular/core";
import { Map as LeafletMap, map, tileLayer } from "leaflet";

import {
  CAPA_BASE,
  CENTRO_INICIAL,
  LIMITES_MUNDO,
  ZOOM_INICIAL,
  ZOOM_MAXIMO,
  ZOOM_MINIMO,
} from "./mapa.config";

@Component({
  selector: "app-mapa",
  templateUrl: "./mapa.component.html",
  styleUrl: "./mapa.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapaComponent implements AfterViewInit, OnDestroy {
  private readonly contenedor =
    viewChild.required<ElementRef<HTMLDivElement>>("contenedorMapa");

  private mapa?: LeafletMap;

  // Leaflet needs the container already in the DOM, so it is created here
  ngAfterViewInit(): void {
    this.mapa = map(this.contenedor().nativeElement, {
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
    }).addTo(this.mapa);
  }

  // Releases Leaflet listeners and layers when the view is destroyed
  ngOnDestroy(): void {
    this.mapa?.remove();
  }
}
