import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  InputSignal,
  OnDestroy,
  OutputEmitterRef,
  Signal,
  WritableSignal,
  effect,
  input,
  output,
  signal,
  untracked,
  viewChild,
} from "@angular/core";
import {
  CircleMarkerOptions,
  LatLngBoundsExpression,
  LatLngExpression,
  LatLngTuple,
  LayerGroup,
  Map as LeafletMap,
  Polyline,
  PolylineOptions,
  circleMarker,
  latLngBounds,
  layerGroup,
  map,
  polyline,
  tileLayer,
} from "leaflet";

import { FarmaciaMapa } from "../../../core/services/farmacia.service";
import { Coordenadas } from "../../../core/services/ubicacion.service";

/**
 *
 * el punto donde se centra el mapa al abrirse: el centro de Medellín
 *
 */
const CENTRO_INICIAL: LatLngExpression = [6.2442, -75.5812];

/**
 *
 * qué tan cerca se ve el mapa al abrirse. 13 muestra la ciudad por barrios
 *
 */
const ZOOM_INICIAL: number = 13;

/**
 *
 * lo más lejos que se puede alejar el mapa (3 muestra continentes)
 *
 */
const ZOOM_MINIMO: number = 3;

/**
 *
 * lo más cerca que se puede acercar el mapa (19 muestra calles y casas)
 *
 */
const ZOOM_MAXIMO: number = 19;

/**
 *
 * los bordes del mapa, para que no se pueda arrastrar fuera del mundo
 *
 * va de -85 a 85 de latitud porque más allá de eso el mapa ya no tiene imágenes
 *
 */
const LIMITES_MUNDO: LatLngBoundsExpression = [
  [-85, -180],
  [85, 180],
];

/**
 *
 * de dónde salen las imágenes del mapa (calles, ríos, barrios)
 *
 * url es la dirección de OpenStreetMap, un mapa libre y gratuito que no pide
 * clave. Las {z}, {x} y {y} las reemplaza Leaflet con el zoom y la posición de
 * cada cuadro. atribucion es el crédito a OpenStreetMap que se muestra en la
 * esquina del mapa, como piden sus condiciones de uso
 *
 */
const CAPA_BASE: Readonly<{ url: string; atribucion: string }> = {
  url: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
  atribucion:
    '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
};

/**
 *
 * cómo se ve cada punto del mapa: un círculo verde de la marca con borde blanco
 *
 */
const ESTILO_MARCADOR: CircleMarkerOptions = {
  radius: 8,
  color: "#ffffff",
  weight: 2,
  fillColor: "#159a63",
  fillOpacity: 1,
};

/**
 *
 * cómo se ve la farmacia elegida: el mismo círculo, más grande y del verde
 * oscuro de los botones, para que se distinga de las demás
 *
 */
const ESTILO_MARCADOR_SELECCIONADO: CircleMarkerOptions = {
  ...ESTILO_MARCADOR,
  radius: 12,
  weight: 3,
  fillColor: "#107a4e",
};

/**
 *
 * cómo se ve el punto del usuario: un círculo naranja (el color de acento de la
 * marca) con borde blanco
 *
 */
const ESTILO_UBICACION: CircleMarkerOptions = {
  radius: 9,
  color: "#ffffff",
  weight: 3,
  fillColor: "#f2a93b",
  fillOpacity: 1,
};

/**
 *
 * cómo se ve la línea de la ruta: verde oscuro, gruesa y un poco transparente
 * para que se vean las calles debajo
 *
 */
const ESTILO_RUTA: PolylineOptions = {
  color: "#107a4e",
  weight: 5,
  opacity: 0.85,
};

/**
 *
 * qué tan cerca se acerca el mapa al elegir una farmacia o al encuadrar los
 * puntos (16 muestra las calles cercanas)
 *
 */
const ZOOM_SELECCION: number = 16;

/**
 *
 * el espacio que se deja en los bordes del mapa al encuadrar los puntos o la
 * ruta, para que no queden pegados al borde: 32 píxeles
 *
 */
const MARGEN_AJUSTE_PX: number = 32;

/**
 *
 * el componente del mapa interactivo. Muestra un mapa en el que se puede
 * mover y hacer zoom, con un punto por cada farmacia
 *
 * usa la librería Leaflet. Quien lo use le pasa las farmacias y él las dibuja.
 * También puede mostrar la ubicación del usuario, resaltar una farmacia y
 * dibujar una ruta, si se los pasan
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
   * las farmacias que se dibujan en el mapa. Las recibe desde afuera, de quien
   * use el componente; si cambian, el mapa se redibuja solo
   *
   */
  readonly farmacias: InputSignal<FarmaciaMapa[]> = input<FarmaciaMapa[]>([]);
  /**
   *
   * el texto que leen los lectores de pantalla para describir el mapa a las
   * personas con discapacidad visual
   *
   */
  readonly descripcion: InputSignal<string> = input<string>("Mapa interactivo");

  /**
   *
   * dónde está el usuario. Si llega, se dibuja como un punto naranja; si es
   * null, no se dibuja nada
   *
   */
  readonly ubicacion: InputSignal<Coordenadas | null> = input<Coordenadas | null>(null);

  /**
   *
   * el id de la farmacia elegida, para resaltarla. Si es null, no hay ninguna
   * elegida
   *
   */
  readonly seleccionada: InputSignal<number | null> = input<number | null>(null);

  /**
   *
   * los puntos del camino que se dibuja como una línea. Si la lista está vacía,
   * no se dibuja ninguna ruta
   *
   */
  readonly ruta: InputSignal<Coordenadas[]> = input<Coordenadas[]>([]);

  /**
   *
   * avisa a quien use el mapa qué farmacia tocó el usuario, enviando su id
   *
   */
  readonly seleccionar: OutputEmitterRef<number> = output<number>();

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
   * el grupo donde se guarda la línea de la ruta, para poder borrarla y
   * dibujar otra
   *
   */
  private readonly capaRuta: LayerGroup = layerGroup();

  /**
   *
   * deja preparado todo lo que el mapa redibuja solo
   *
   * cada effect se ejecuta solo cuando cambia algo de lo que usa. Si el mapa
   * todavía no existe, no hacen nada:
   * - el primero borra los puntos viejos y dibuja un círculo por cada farmacia,
   *   con su nombre. La elegida va más grande y al final, para que quede encima.
   *   Tocar un punto avisa qué farmacia se eligió. Si hay ubicación del usuario,
   *   también la dibuja
   * - el segundo, cuando llega la ubicación del usuario o cambian las farmacias,
   *   mueve el mapa para que se vean el usuario y todas las farmacias
   * - el tercero, cuando se elige una farmacia, vuela hasta ella
   * - el cuarto borra la ruta vieja y, si hay una nueva, la dibuja y mueve el
   *   mapa para que se vea completa
   *
   */
  constructor() {
    effect(() => {
      if (!this.mapa()) {
        return;
      }

      this.capaMarcadores.clearLayers();
      const seleccionada: number | null = this.seleccionada();
      const farmacias: FarmaciaMapa[] = [...this.farmacias()].sort(
        (a: FarmaciaMapa, b: FarmaciaMapa): number =>
          Number(a.id === seleccionada) - Number(b.id === seleccionada),
      );
      for (const farmacia of farmacias) {
        circleMarker(
          [farmacia.latitud, farmacia.longitud],
          farmacia.id === seleccionada ? ESTILO_MARCADOR_SELECCIONADO : ESTILO_MARCADOR,
        )
          .bindTooltip(farmacia.nombre)
          .on("click", (): void => this.seleccionar.emit(farmacia.id))
          .addTo(this.capaMarcadores);
      }

      const ubicacion: Coordenadas | null = this.ubicacion();
      if (ubicacion) {
        circleMarker([ubicacion.latitud, ubicacion.longitud], ESTILO_UBICACION)
          .bindTooltip("Tu ubicación")
          .addTo(this.capaMarcadores);
      }
    });

    effect(() => {
      const mapa: LeafletMap | undefined = this.mapa();
      const ubicacion: Coordenadas | null = this.ubicacion();
      if (!mapa || !ubicacion) {
        return;
      }

      const puntos: LatLngTuple[] = [
        [ubicacion.latitud, ubicacion.longitud],
        ...this.farmacias().map(
          (farmacia: FarmaciaMapa): LatLngTuple => [farmacia.latitud, farmacia.longitud],
        ),
      ];
      mapa.fitBounds(latLngBounds(puntos), {
        padding: [MARGEN_AJUSTE_PX, MARGEN_AJUSTE_PX],
        maxZoom: ZOOM_SELECCION,
      });
    });

    effect(() => {
      const mapa: LeafletMap | undefined = this.mapa();
      const seleccionada: number | null = this.seleccionada();
      if (!mapa || seleccionada === null) {
        return;
      }

      const farmacia: FarmaciaMapa | undefined = untracked(this.farmacias).find(
        (farmacia: FarmaciaMapa): boolean => farmacia.id === seleccionada,
      );
      if (farmacia) {
        mapa.flyTo([farmacia.latitud, farmacia.longitud], Math.max(mapa.getZoom(), ZOOM_SELECCION));
      }
    });

    effect(() => {
      const mapa: LeafletMap | undefined = this.mapa();
      if (!mapa) {
        return;
      }

      this.capaRuta.clearLayers();
      const puntos: LatLngTuple[] = this.ruta().map(
        (punto: Coordenadas): LatLngTuple => [punto.latitud, punto.longitud],
      );
      if (puntos.length > 1) {
        const linea: Polyline = polyline(puntos, ESTILO_RUTA).addTo(this.capaRuta);
        mapa.flyToBounds(linea.getBounds(), {
          padding: [MARGEN_AJUSTE_PX, MARGEN_AJUSTE_PX],
          maxZoom: ZOOM_SELECCION,
        });
      }
    });
  }

  /**
   *
   * crea el mapa cuando el HTML del componente ya está en pantalla
   *
   * lo centra en Medellín con los límites de zoom y de bordes de arriba, le
   * pone las imágenes de OpenStreetMap, la capa de la ruta y la capa de puntos
   * encima, para que la línea no tape los puntos
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

    this.capaRuta.addTo(mapa);
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
