import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  computed,
  inject,
  output,
  signal,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Subscription } from "rxjs";

import {
  FiltrosMedicamentos,
  MedicamentoBusqueda,
  MedicamentoService,
  PaginaMedicamentos,
} from "../../../../core/services/medicamento.service";

type CampoBusqueda = "nombre" | "principio" | "todos";
type OrdenBusqueda = "relevancia" | "precio";

/**
 *
 * el buscador del panel del paciente/cuidador (HU-3)
 *
 * reúne en una sola tarjeta el texto, el campo (nombre o principio activo), la
 * farmacia y el rango de precio, y muestra los resultados paginados. Cada
 * resultado se puede tocar para abrir su detalle
 *
 */
@Component({
  selector: "app-buscador-paciente",
  templateUrl: "./buscador-medicamentos.component.html",
  styleUrl: "./buscador-medicamentos.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BuscadorPacienteComponent {
  private readonly medicamentoService: MedicamentoService = inject(MedicamentoService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);
  private busquedaActual: Subscription | null = null;

  /** avisa el medicamento que el usuario tocó para abrir su detalle */
  readonly seleccionar = output<number>();

  /** avisa que empezó una búsqueda nueva, para cerrar el detalle anterior */
  readonly buscarIniciado = output<void>();

  protected readonly texto = signal("");
  protected readonly campo = signal<CampoBusqueda>("todos");
  protected readonly filtroFarmacia = signal(false);
  protected readonly filtroPrecio = signal(false);
  protected readonly farmacia = signal("");
  protected readonly precioMin = signal<number | null>(null);
  protected readonly precioMax = signal<number | null>(null);
  protected readonly orden = signal<OrdenBusqueda>("relevancia");
  protected readonly pagina = signal(0);
  protected readonly tamano = 10;

  protected readonly cargando = signal(false);
  protected readonly error = signal("");
  protected readonly buscado = signal(false);
  protected readonly paginaResultados = signal<PaginaMedicamentos | null>(null);
  protected readonly seleccionado = signal<number | null>(null);

  protected readonly sinResultados = computed((): boolean => {
    const pagina = this.paginaResultados();
    return this.buscado() && !this.cargando() && (pagina?.total ?? 0) === 0;
  });

  protected readonly hayCriterios = computed((): boolean => {
    const texto: string = this.texto().trim();
    const farmacia: string = this.filtroFarmacia() ? this.farmacia().trim() : "";
    return texto.length >= 2 || farmacia.length >= 2
      || (this.filtroPrecio() && (this.precioMin() !== null || this.precioMax() !== null));
  });

  protected readonly hayAnterior = computed(
    (): boolean => (this.paginaResultados()?.pagina ?? 0) > 0,
  );
  protected readonly haySiguiente = computed(
    (): boolean => this.paginaResultados()?.hayMas ?? false,
  );

  private readonly formatoPrecio: Intl.NumberFormat = new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  });

  constructor() {
    this.destroyRef.onDestroy((): void => this.busquedaActual?.unsubscribe());
    this.buscar(0);
  }

  protected alternarCampo(campo: "nombre" | "principio"): void {
    this.campo.update((actual: CampoBusqueda): CampoBusqueda =>
      actual === campo ? "todos" : campo,
    );
  }

  protected alternarFarmacia(): void {
    this.filtroFarmacia.update((activo: boolean): boolean => !activo);
    if (!this.filtroFarmacia()) {
      this.farmacia.set("");
    }
  }

  protected alternarPrecio(): void {
    this.filtroPrecio.update((activo: boolean): boolean => !activo);
    if (!this.filtroPrecio()) {
      this.precioMin.set(null);
      this.precioMax.set(null);
    }
  }

  protected cambiarOrden(valor: string): void {
    this.orden.set(valor === "precio" ? "precio" : "relevancia");
    this.buscar(0);
  }

  protected escribirNumero(evento: Event, destino: "min" | "max"): void {
    const valor: string = (evento.target as HTMLInputElement).value;
    const numero: number | null = valor === "" ? null : Number(valor);
    const limpio: number | null = numero !== null && Number.isFinite(numero) && numero >= 0
      ? numero
      : null;
    if (destino === "min") {
      this.precioMin.set(limpio);
    } else {
      this.precioMax.set(limpio);
    }
  }

  protected limpiar(): void {
    this.texto.set("");
    this.campo.set("todos");
    this.filtroFarmacia.set(false);
    this.filtroPrecio.set(false);
    this.farmacia.set("");
    this.precioMin.set(null);
    this.precioMax.set(null);
    this.orden.set("relevancia");
    this.buscar(0);
  }

  protected buscar(nuevaPagina: number): void {
    this.busquedaActual?.unsubscribe();
    this.pagina.set(Math.max(0, nuevaPagina));
    this.cargando.set(true);
    this.error.set("");
    this.buscado.set(true);
    this.seleccionado.set(null);
    this.buscarIniciado.emit();

    this.busquedaActual = this.medicamentoService
      .buscarConFiltros(this.filtros(this.pagina()))
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (pagina: PaginaMedicamentos): void => {
          this.paginaResultados.set(pagina);
          this.cargando.set(false);
        },
        error: (): void => {
          this.paginaResultados.set(null);
          this.error.set("No se pudo buscar. Revisa tu conexión e intenta de nuevo.");
          this.cargando.set(false);
        },
      });
  }

  protected paginaAnterior(): void {
    if (this.hayAnterior()) {
      this.buscar(this.pagina() - 1);
    }
  }

  protected paginaSiguiente(): void {
    if (this.haySiguiente()) {
      this.buscar(this.pagina() + 1);
    }
  }

  protected abrir(medicamento: MedicamentoBusqueda): void {
    this.seleccionado.set(medicamento.id);
    this.seleccionar.emit(medicamento.id);
  }

  protected formatearPrecio(valor: number | null): string {
    return valor === null ? "Sin precio reportado" : this.formatoPrecio.format(valor);
  }

  protected textoPrecio(valor: number | null): string {
    return valor === null ? "" : String(valor);
  }

  private filtros(pagina: number): FiltrosMedicamentos {
    return {
      texto: this.texto(),
      campo: this.campo(),
      farmacia: this.filtroFarmacia() ? this.farmacia() : "",
      precioMin: this.filtroPrecio() ? this.precioMin() : null,
      precioMax: this.filtroPrecio() ? this.precioMax() : null,
      orden: this.orden(),
      pagina,
      tamano: this.tamano,
    };
  }
}
