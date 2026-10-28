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
import { switchMap } from "rxjs";

import {
  EstadoFarmacia,
  FarmaciaHistorial,
  PanelAdminService,
} from "../../../core/services/panel-admin.service";
import { formatearFechaColombia } from "../../../core/services/panel-farmacia.service";
import { EstadoLista, aEstadoLista } from "../estado-lista";
import { FarmaciaDetalleAdminComponent } from "./farmacia-detalle-admin.component";

/**
 * 
 * cuántas farmacias se muestran por página
 * 
 */
const TAMANO_PAGINA: number = 10;

/**
 * 
 * el historial de farmacias del administrador: la tabla de todas las farmacias con filtro
 * por estado y paginación. Al abrir una se muestra su detalle (auditoría y publicaciones)
 * en lugar de la tabla, y al volver se conservan el filtro y la página
 * 
 */
@Component({
  selector: "app-farmacias-admin",
  imports: [FarmaciaDetalleAdminComponent],
  templateUrl: "./farmacias-admin.component.html",
  styleUrl: "../panel-admin-comun.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FarmaciasAdminComponent {
  private readonly panel: PanelAdminService = inject(PanelAdminService);

  protected readonly formatearFecha: (fecha: string) => string = formatearFechaColombia;

  protected readonly estadoFiltro: WritableSignal<EstadoFarmacia | ""> = signal("");
  protected readonly pagina: WritableSignal<number> = signal(0);
  private readonly recarga: WritableSignal<number> = signal(0);

  private readonly consulta: Signal<{ estado: EstadoFarmacia | ""; pagina: number; recarga: number }> = computed(() => ({
    estado: this.estadoFiltro(),
    pagina: this.pagina(),
    recarga: this.recarga(),
  }));

  protected readonly estado: Signal<EstadoLista<FarmaciaHistorial>> = toSignal(
    toObservable(this.consulta).pipe(
      switchMap((consulta) =>
        aEstadoLista(this.panel.listarFarmacias(consulta.estado, consulta.pagina, TAMANO_PAGINA)),
      ),
    ),
    { initialValue: { tipo: "cargando" } as EstadoLista<FarmaciaHistorial> },
  );

  /**
   * 
   * la farmacia cuyo detalle está abierto. Es null mientras se ve la tabla
   * 
   */
  protected readonly seleccionada: WritableSignal<FarmaciaHistorial | null> = signal(null);

  /**
   * 
   * al cambiar el filtro se vuelve a la primera página, porque la que se estaba viendo
   * puede no existir con el filtro nuevo
   * 
   */
  protected alCambiarEstado(valor: string): void {
    this.estadoFiltro.set(valor as EstadoFarmacia | "");
    this.pagina.set(0);
  }

  protected irAPagina(numero: number): void {
    this.pagina.set(numero);
  }

  protected abrirDetalle(farmacia: FarmaciaHistorial): void {
    this.seleccionada.set(farmacia);
  }

  /**
   * 
   * al volver se pide la tabla de nuevo: en el detalle se pudieron eliminar calificaciones
   * y las alertas pendientes pudieron cambiar
   * 
   */
  protected cerrarDetalle(): void {
    this.seleccionada.set(null);
    this.recarga.update((n) => n + 1);
  }
}