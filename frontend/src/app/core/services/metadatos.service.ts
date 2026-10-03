import { DOCUMENT } from "@angular/common";
import { Injectable, inject } from "@angular/core";
import { Meta } from "@angular/platform-browser";

const COLOR_TEMA_CLARO: string = "#159a63";
const COLOR_TEMA_OSCURO: string = "#131210";
const RUTA_FAVICON: string = "assets/icons/favicon.svg";

@Injectable({ providedIn: "root" })
export class MetadatosService {
  private readonly documento: Document = inject(DOCUMENT);
  private readonly meta: Meta = inject(Meta);

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

  definirDescripcion(descripcion: string): void {
    this.meta.updateTag({ name: "description", content: descripcion });
  }
}
