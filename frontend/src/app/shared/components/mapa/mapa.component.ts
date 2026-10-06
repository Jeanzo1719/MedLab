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
 * 
 * el componente del mapa interactivo. Muestra un mapa en el que se puede
 * mover y hacer zoom, con puntos encima
 * 
 * usa la librería Leaflet. Está en shared porque no depende de ninguna página:
 * quien lo use le pasa los puntos y él los dibuja
 * 
 */
@Component({
  selector: "app-mapa",
  templateUrl: "./mapa.component.html",
  styleUrl: "./mapa.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapaComponent implements AfterViewInit, OnDestroy {
  /**
   * 
   * los puntos que se dibujan en el mapa. Los recibe desde afuera, de quien
   * use el componente; si cambian, el mapa se redibuja solo
   * 
   */
  readonly marcadores: InputSignal<MarcadorMapa[]> = input<MarcadorMapa[]>([]);
  /**
   * 
   * el texto que leen los lectores de pantalla para describir el mapa a las
   * personas con discapacidad visual
   * 
   */
  readonly descripcion: InputSignal<string> = input<string>("Mapa interactivo");

  /**
   * 
   * el div del HTML donde Leaflet dibuja el mapa
   * 
   */
  private readonly contenedor: Signal<ElementRef<HTMLDivElement>> =
    viewChild.required<ElementRef<HTMLDivElement>>("contenedorMapa");

  /**
   * 
   * el mapa de Leaflet. Empieza vacío (undefined) y se llena cuando el HTML ya
   * está en pantalla
   * 
   */
  private readonly mapa: WritableSignal<LeafletMap | undefined> =
    signal<LeafletMap | undefined>(undefined);
  /**
   * 
   * el grupo donde se guardan todos los puntos, para poder borrarlos y
   * volverlos a dibujar juntos
   * 
   */
  private readonly capaMarcadores: LayerGroup = layerGroup();

  /**
   * 
   * deja preparado el redibujo de los puntos
   * 
   * el effect se ejecuta solo cada vez que cambian los marcadores o el mapa:
   * borra los puntos viejos y dibuja un círculo por cada marcador, con su
   * etiqueta. Si el mapa todavía no existe, no hace nada
   * 
   */
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

  /**
   * 
   * crea el mapa cuando el HTML del componente ya está en pantalla
   * 
   * lo centra en Medellín con los límites de zoom y de bordes de mapa.config, le
   * pone las imágenes de OpenStreetMap y la capa de puntos encima
   * 
   */
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

  /**
   * 
   * borra el mapa cuando el componente sale de la pantalla, para liberar memoria
   * 
   */
  ngOnDestroy(): void {
    this.mapa()?.remove();
  }
}
