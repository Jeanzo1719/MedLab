import { DOCUMENT } from "@angular/common";
import { Injectable, inject } from "@angular/core";
import { Meta } from "@angular/platform-browser";

/** Brand colours for the browser UI, same values as --color-marca and --color-negro */
const COLOR_TEMA_CLARO: string = "#159a63";
const COLOR_TEMA_OSCURO: string = "#131210";
/** SVG favicon; it switches to the dark variant with prefers-color-scheme */
const RUTA_FAVICON: string = "assets/icons/favicon.svg";

/**
 * Document metadata (language, theme colour, favicon and description) set from
 * Angular instead of index.html, so each page can declare its own SEO data.
 */
@Injectable({ providedIn: "root" })
export class MetadatosService {
  private readonly documento: Document = inject(DOCUMENT);
  private readonly meta: Meta = inject(Meta);

  /** App-wide metadata; called once from the app shell */
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

  /** Page description shown by search engines; each page sets its own */
  definirDescripcion(descripcion: string): void {
    this.meta.updateTag({ name: "description", content: descripcion });
  }
}
