import { ChangeDetectionStrategy, Component, Signal, inject } from "@angular/core";

import { Tema, TemaService } from "../../../core/services/tema.service";

/**
 *
 * el botón que cambia entre modo claro y oscuro. El tema se guarda y se aplica
 * a toda la página, así que el cambio se ve igual en cualquier vista
 *
 * es el mismo botón de la landing, empaquetado para reutilizarlo en los paneles,
 * el inicio de sesión y el registro
 *
 */
@Component({
  selector: "app-boton-tema",
  templateUrl: "./boton-tema.component.html",
  styleUrl: "./boton-tema.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BotonTemaComponent {
  private readonly temaService: TemaService = inject(TemaService);

  protected readonly tema: Signal<Tema> = this.temaService.tema;

  /**
   *
   * cambia al tema contrario (claro ↔ oscuro) y lo recuerda en el navegador
   *
   */
  protected alternar(): void {
    this.temaService.alternar();
  }
}
