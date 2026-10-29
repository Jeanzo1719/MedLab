import { ChangeDetectionStrategy, Component, DestroyRef, WritableSignal, inject, signal } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { EMPTY, Subject, catchError, map, of, startWith, switchMap } from "rxjs";

import { DisponibilidadSospechosa, PanelAdminService } from "../../../core/services/panel-admin.service";
import { mensajeDeError } from "../../../core/services/panel-farmacia.service";
import { StompService } from "../../../core/services/stomp.service";
import { CalificacionesDisponibilidadComponent } from "../calificaciones-disponibilidad/calificaciones-disponibilidad.component";

/**
 * 
 * el topic donde el backend avisa que una publicación pasó el umbral de calificaciones
 * "No hay". Debe ser igual a NotificadorAlertas.DESTINO_ADMIN_CALIFICACIONES
 * 
 */
const DESTINO_AVISOS: string = "/topic/admin/calificaciones";

/**
 * 
 * resultado de pedir la lista: las filas, o el mensaje de error si falló
 * 
 */
interface ResultadoCarga {
  filas: DisponibilidadSospechosa[] | null;
  error: string | null;
}

/**
 * 
 * las publicaciones con muchas calificaciones "No hay" entre las últimas 20, que pueden
 * ser falsas. Se actualiza sola cuando el backend avisa por WebSocket, y desde aquí se
 * abren las calificaciones de cada una para eliminar las falsas
 * 
 * al llegar un aviso no se mezcla el dato con la lista: se muestra el aviso y se vuelve a
 * pedir la lista, así la tabla siempre es lo que dice el backend
 * 
 */
@Component({
  selector: "app-calificaciones-admin",
  imports: [CalificacionesDisponibilidadComponent],
  templateUrl: "./calificaciones-admin.component.html",
  styleUrl: "../panel-admin-comun.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalificacionesAdminComponent {
  private readonly panel: PanelAdminService = inject(PanelAdminService);
  private readonly stomp: StompService = inject(StompService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  /**
   * 
   * las mismas reglas del backend (CalificacionesSospechosasService), solo para mostrarlas
   * 
   */
  protected readonly umbral: number = 15;
  protected readonly ventana: number = 20;

  protected readonly filas: WritableSignal<DisponibilidadSospechosa[]> = signal([]);
  protected readonly cargando: WritableSignal<boolean> = signal(true);
  protected readonly errorCarga: WritableSignal<string | null> = signal(null);

  /**
   * 
   * el último aviso en vivo que llegó, para mostrarlo arriba hasta que se cierre
   * 
   */
  protected readonly avisoVivo: WritableSignal<DisponibilidadSospechosa | null> = signal(null);

  /**
   * 
   * la publicación cuyas calificaciones se están viendo. Es null si no hay ninguna abierta
   * 
   */
  protected readonly elegida: WritableSignal<DisponibilidadSospechosa | null> = signal(null);

  private readonly recarga: Subject<void> = new Subject<void>();

  constructor() {
    // switchMap: si llegan varios avisos seguidos, solo vale la última respuesta.
    // Si falla, se conserva la lista anterior y se muestra el error.
    this.recarga
      .pipe(
        startWith(undefined),
        switchMap(() =>
          this.panel.listarSospechosas().pipe(
            map((filas): ResultadoCarga => ({ filas, error: null })),
            catchError((error: unknown) => of<ResultadoCarga>({ filas: null, error: mensajeDeError(error) })),
          ),
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((resultado: ResultadoCarga) => {
        if (resultado.filas) {
          this.filas.set(resultado.filas);
        }
        this.errorCarga.set(resultado.error);
        this.cargando.set(false);
      });

    this.stomp
      .escuchar<DisponibilidadSospechosa>(DESTINO_AVISOS)
      .pipe(
        catchError(() => EMPTY),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((aviso: DisponibilidadSospechosa) => {
        this.avisoVivo.set(aviso);
        this.recarga.next();
      });
  }

  protected actualizar(): void {
    this.recarga.next();
  }

  protected cerrarAviso(): void {
    this.avisoVivo.set(null);
  }

  protected verCalificaciones(publicacion: DisponibilidadSospechosa): void {
    this.elegida.set(publicacion);
  }

  protected cerrarCalificaciones(): void {
    this.elegida.set(null);
  }

  /**
   * 
   * al eliminar una calificación la publicación puede dejar de ser sospechosa, así que se
   * vuelve a pedir la lista
   * 
   */
  protected alEliminarCalificacion(): void {
    this.recarga.next();
  }
}