import {
  ChangeDetectionStrategy,
  Component,
  Signal,
  WritableSignal,
  inject,
  signal,
} from "@angular/core";
import { Router, RouterLink } from "@angular/router";

import { Sesion, SesionService, rutaInicio } from "../../../core/services/sesion.service";
import { Tema, TemaService } from "../../../core/services/tema.service";

/**
 * 
 * el encabezado de la landing: el logo, el menú de secciones y los botones de
 * cuenta (iniciar sesión y registrarse, o el panel y cerrar sesión)
 * 
 * se queda pegado arriba al bajar por la página. En celular y tablet el menú se
 * esconde detrás de un botón (☰); en computador se ve completo
 * 
 */
@Component({
  selector: "app-encabezado",
  imports: [RouterLink],
  templateUrl: "./encabezado.component.html",
  styleUrl: "./encabezado.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EncabezadoComponent {
  private readonly temaService: TemaService = inject(TemaService);
  private readonly sesionService: SesionService = inject(SesionService);
  private readonly router: Router = inject(Router);

  /**
   * 
   * si el menú está abierto o cerrado en celular y tablet. Empieza cerrado
   * 
   */
  protected readonly menuAbierto: WritableSignal<boolean> = signal(false);
  protected readonly tema: Signal<Tema> = this.temaService.tema;
  protected readonly sesion: Signal<Sesion | null> = this.sesionService.sesionActual;

  /** La ruta del panel de cada rol, para el enlace "Mi cuenta". */
  protected readonly rutaInicio: (rol: Sesion["rol"]) => string = rutaInicio;

  /**
   * 
   * abre el menú si está cerrado y lo cierra si está abierto. Lo usa el botón ☰
   * 
   */
  protected alternarMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  /**
   * 
   * cierra el menú. Se usa al tocar un enlace, para que el menú no tape la
   * sección a la que se saltó
   * 
   */
  protected cerrarMenu(): void {
    this.menuAbierto.set(false);
  }

  protected alternarTema(): void {
    this.temaService.alternar();
  }

  /**
   * 
   * cierra la sesión desde cualquier vista y vuelve a la landing
   * 
   */
  protected cerrarSesion(): void {
    this.cerrarMenu();
    this.sesionService.cerrarSesion();
    void this.router.navigateByUrl("/");
  }
}
