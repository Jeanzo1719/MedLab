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

/**
 * Mapa interactivo reutilizable (capa shared).
 *
 * Qué es: un componente genérico que envuelve Leaflet con teselas de
 * OpenStreetMap y no conoce el dominio (no sabe qué es una farmacia).
 *
 * Cómo funciona: crea el mapa en ngAfterViewInit, cuando el contenedor ya está
 * en el DOM, con la configuración de mapa.config.ts. Un effect redibuja los
 * marcadores cada vez que cambia el input marcadores, y ngOnDestroy libera
 * Leaflet. El input descripcion da el nombre accesible de la región.
 *
 * Para qué sirve: cualquier página puede mostrar puntos en un mapa convirtiendo
 * sus datos a MarcadorMapa.
 */
@Component({
  selector: "app-mapa",
  templateUrl: "./mapa.component.html",
  styleUrl: "./mapa.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapaComponent implements AfterViewInit, OnDestroy {
  readonly marcadores: InputSignal<MarcadorMapa[]> = input<MarcadorMapa[]>([]);
  /** Nombre accesible de la región del mapa; cada página describe lo que muestra su mapa */
  readonly descripcion: InputSignal<string> = input<string>("Mapa interactivo");

  private readonly contenedor: Signal<ElementRef<HTMLDivElement>> =
    viewChild.required<ElementRef<HTMLDivElement>>("contenedorMapa");

  // Es una signal para que el effect de los marcadores se vuelva a ejecutar cuando el mapa exista
  private readonly mapa: WritableSignal<LeafletMap | undefined> =
    signal<LeafletMap | undefined>(undefined);
  private readonly capaMarcadores: LayerGroup = layerGroup();

  constructor() {
    // Redibuja todos los marcadores cada vez que cambia el input
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

  // Leaflet necesita el contenedor ya en el DOM, por eso el mapa se crea aquí
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

  // Libera los listeners y las capas de Leaflet cuando se destruye la vista
  ngOnDestroy(): void {
    this.mapa()?.remove();
  }
}
