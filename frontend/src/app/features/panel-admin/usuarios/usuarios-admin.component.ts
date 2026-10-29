import {
  ChangeDetectionStrategy,
  Component,
  Signal,
  WritableSignal,
  computed,
  inject,
  signal,
} from "@angular/core";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { Observable, catchError, map, of, startWith, switchMap } from "rxjs";

import { PanelAdminService, UsuarioHistorial } from "../../../core/services/panel-admin.service";
import {
  Pagina,
  formatearFechaColombia,
  mensajeDeError,
} from "../../../core/services/panel-farmacia.service";
import { RolUsuario } from "../../../core/services/sesion.service";

/**
 * 
 * cuántos usuarios se muestran por página
 * 
 */
const TAMANO_PAGINA: number = 10;

/**
 * 
 * los estados en que puede estar la tabla, para saber qué mostrar
 * 
 */
type EstadoUsuarios =
  | { tipo: "cargando" }
  | { tipo: "listo"; resultado: Pagina<UsuarioHistorial> }
  | { tipo: "error"; mensaje: string };

/**
 * 
 * el historial de usuarios del administrador: la tabla de todos los usuarios, con filtro
 * por rol y paginación
 * 
 */
@Component({
  selector: "app-usuarios-admin",
  templateUrl: "./usuarios-admin.component.html",
  styleUrl: "../panel-admin-comun.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UsuariosAdminComponent {
  private readonly panel: PanelAdminService = inject(PanelAdminService);

  protected readonly etiquetaRol: Record<RolUsuario, string> = {
    paciente: "Paciente",
    farmacia: "Farmacia",
    administrador: "Administrador",
  };
  protected readonly formatearFecha: (fecha: string) => string = formatearFechaColombia;

  /**
   * 
   * el rol por el que se filtra (vacío es todos) y la página que se está viendo
   * (la primera es la 0)
   * 
   */
  protected readonly rol: WritableSignal<RolUsuario | ""> = signal("");
  protected readonly pagina: WritableSignal<number> = signal(0);

  private readonly consulta: Signal<{ rol: RolUsuario | ""; pagina: number }> = computed(() => ({
    rol: this.rol(),
    pagina: this.pagina(),
  }));

  /**
   * 
   * el estado de la tabla. Cada vez que cambia el rol o la página vuelve a pedir la lista;
   * si había una petición en camino, se cancela y solo vale la última
   * 
   */
  protected readonly estado: Signal<EstadoUsuarios> = toSignal(
    toObservable(this.consulta).pipe(switchMap((consulta) => this.cargar(consulta.rol, consulta.pagina))),
    { initialValue: { tipo: "cargando" } as EstadoUsuarios },
  );

  /**
   * 
   * al cambiar el filtro se vuelve a la primera página, porque la que se estaba viendo
   * puede no existir con el filtro nuevo
   * 
   */
  protected alCambiarRol(valor: string): void {
    this.rol.set(valor as RolUsuario | "");
    this.pagina.set(0);
  }

  protected irAPagina(numero: number): void {
    this.pagina.set(numero);
  }

  private cargar(rol: RolUsuario | "", pagina: number): Observable<EstadoUsuarios> {
    return this.panel.listarUsuarios(rol, pagina, TAMANO_PAGINA).pipe(
      map((resultado): EstadoUsuarios => ({ tipo: "listo", resultado })),
      catchError((error: unknown) => of<EstadoUsuarios>({ tipo: "error", mensaje: mensajeDeError(error) })),
      startWith<EstadoUsuarios>({ tipo: "cargando" }),
    );
  }
}