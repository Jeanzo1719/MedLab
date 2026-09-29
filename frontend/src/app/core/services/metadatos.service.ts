import { DOCUMENT } from "@angular/common";
import { Injectable, inject } from "@angular/core";
import { Meta } from "@angular/platform-browser";

/**
 * Colores de marca para la interfaz del navegador; mismos valores que
 * --color-marca y --color-negro de styles.css
 */
const COLOR_TEMA_CLARO: string = "#159a63";
const COLOR_TEMA_OSCURO: string = "#131210";
/** Favicon SVG; cambia a la variante oscura con prefers-color-scheme */
const RUTA_FAVICON: string = "assets/icons/favicon.svg";

/**
 * Servicio de metadatos del documento (capa core, servicios).
 *
 * Qué es: el responsable del SEO y de los datos del <head> (idioma, color de
 * tema, favicon y descripción).
 *
 * Cómo funciona: usa las APIs Meta y DOCUMENT de Angular. aplicarGenerales()
 * pone los metadatos de toda la app y lo llama provideAppInitializer al
 * arrancar; definirDescripcion() la llama cada página con su propio texto.
 *
 * Para qué sirve: cumple el SEO de la HU-10 sin modificar index.html, como pidió
 * la revisión de la PR.
 */
@Injectable({ providedIn: "root" })
export class MetadatosService {
  private readonly documento: Document = inject(DOCUMENT);
  private readonly meta: Meta = inject(Meta);

  /**
   * Metadatos de toda la app; los aplica una sola vez, al arrancar,
   * provideAppInitializer en app.config.ts
   */
  aplicarGenerales(): void {
    this.documento.documentElement.lang = "es";

    this.meta.addTags([
      {
        name: "theme-color",
        content: COLOR_TEMA_CLARO,
        media: "(prefers-color-scheme: light)",
      },
      {
        name: "theme-color",
        content: COLOR_TEMA_OSCURO,
        media: "(prefers-color-scheme: dark)",
      },
    ]);

    const favicon: HTMLLinkElement = this.documento.createElement("link");
    favicon.rel = "icon";
    favicon.type = "image/svg+xml";
    favicon.href = RUTA_FAVICON;
    this.documento.head.appendChild(favicon);
  }

  /** Descripción que muestran los buscadores; cada página define la suya */
  definirDescripcion(descripcion: string): void {
    this.meta.updateTag({ name: "description", content: descripcion });
  }
}
