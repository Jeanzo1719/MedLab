import { DOCUMENT } from "@angular/common";
import { Injectable, inject } from "@angular/core";
import { Meta } from "@angular/platform-browser";

/**
 * 
 * el color de la barra del navegador en el celular cuando está en modo claro
 * (el verde de la marca)
 * 
 */
const COLOR_TEMA_CLARO: string = "#159a63";
/**
 * 
 * el color de la barra del navegador en el celular cuando está en modo oscuro
 * 
 */
const COLOR_TEMA_OSCURO: string = "#131210";
/**
 * 
 * la ruta del ícono que se ve en la pestaña del navegador
 * 
 */
const RUTA_FAVICON: string = "assets/icons/favicon.svg";

/**
 * 
 * el service de los metadatos de la página: los datos que no se ven en
 * pantalla pero que leen el navegador y los buscadores como Google
 * 
 * existe para no modificar el index.html, que viene de develop
 * 
 */
@Injectable({ providedIn: "root" })
export class MetadatosService {
  /**
   * 
   * la página HTML completa, para poder agregarle cosas
   * 
   */
  private readonly documento: Document = inject(DOCUMENT);
  /**
   * 
   * la herramienta de Angular para agregar y cambiar etiquetas meta del HTML
   * 
   */
  private readonly meta: Meta = inject(Meta);

  /**
   * 
   * aplica los datos generales de la página una sola vez, al arrancar la app
   * 
   * marca el idioma de la página como español, agrega el color de la barra del
   * navegador para modo claro y oscuro, y agrega el favicon
   * 
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

  /**
   * 
   * pone la descripción de la página, el texto que muestran los buscadores
   * debajo del título
   * 
   * cada página la define al abrirse, por ejemplo la landing
   * 
   */
  definirDescripcion(descripcion: string): void {
    this.meta.updateTag({ name: "description", content: descripcion });
  }
}
