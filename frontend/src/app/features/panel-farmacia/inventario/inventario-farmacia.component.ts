import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  Signal,
  WritableSignal,
  computed,
  inject,
  signal,
} from "@angular/core";
import { takeUntilDestroyed, toObservable, toSignal } from "@angular/core/rxjs-interop";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from "@angular/forms";
import {
  Observable,
  catchError,
  debounceTime,
  distinctUntilChanged,
  map,
  of,
  startWith,
  switchMap,
  tap,
} from "rxjs";

import {
  MedicamentoBusqueda,
  MedicamentoService,
} from "../../../core/services/medicamento.service";
import {
  Catalogos,
  EstadoProducto,
  GuardarProducto,
  GuardarProductoManual,
  OpcionCatalogo,
  Pagina,
  PanelFarmaciaService,
  ProductoInventario,
  formatearFechaColombia,
  mensajeDeError,
} from "../../../core/services/panel-farmacia.service";

/**
 * 
 * cuántos productos se muestran por página
 * 
 */
const TAMANO_PAGINA: number = 10;
/**
 * 
 * cuánto se espera después de la última tecla antes de buscar: 300 milisegundos
 * 
 */
const ESPERA_ESCRITURA_MS: number = 300;
/**
 * 
 * la cantidad mínima de letras para buscar un medicamento al agregar un producto
 * 
 */
const LONGITUD_MINIMA_BUSQUEDA: number = 2;

/**
 * 
 * quita los espacios de los bordes y junta los repetidos, igual que hace el backend antes
 * de buscar el medicamento: así lo que se valida aquí es lo que se guarda
 * 
 */
function limpiarTexto(valor: string): string {
  return valor.trim().replace(/\s+/g, " ");
}

/**
 * 
 * valida que el texto, ya limpio, tenga entre min y max caracteres. Es la misma regla que
 * aplica el backend (el CHECK de la base de datos cuenta el texto limpio)
 * 
 */
function largoLimpio(min: number, max: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const largo: number = limpiarTexto(String(control.value ?? "")).length;
    return largo >= min && largo <= max ? null : { largo: { min, max } };
  };
}

/**
 * 
 * los estados en que puede estar la tabla del inventario, para saber qué mostrar
 * 
 */
type EstadoInventario =
  | { tipo: "cargando" }
  | { tipo: "listo"; resultado: Pagina<ProductoInventario> }
  | { tipo: "error"; mensaje: string };

/**
 * 
 * el medicamento al que se le está guardando el producto: al crear se elige de
 * la búsqueda; al editar viene de la fila
 * 
 */
interface MedicamentoElegido {
  id: number;
  nombreComercial: string;
  detalle: string;
}

/**
 * 
 * un mensaje de resultado que se muestra arriba de la tabla, de éxito o de error
 * 
 */
interface Aviso {
  tipo: "ok" | "error";
  texto: string;
}

/**
 * 
 * el inventario de la farmacia: la tabla de sus productos con búsqueda, paginación,
 * y las acciones de agregar, editar y dar de baja
 * 
 * dar de baja no borra: deja el producto en 0 y agotado, y por eso pide confirmación.
 * Crear y editar usan el mismo formulario y la misma llamada, porque el backend
 * crea el producto o actualiza el que ya existía
 * 
 * HU-7: además de elegir un medicamento del catálogo, la farmacia puede reportar uno
 * escribiendo sus datos (formulario "manual"). El backend reutiliza el medicamento si ya
 * existe con esa combinación y, si no, lo crea
 * 
 */
@Component({
  selector: "app-inventario-farmacia",
  imports: [ReactiveFormsModule],
  templateUrl: "./inventario-farmacia.component.html",
  styleUrl: "./inventario-farmacia.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InventarioFarmaciaComponent {
  private readonly panel: PanelFarmaciaService = inject(PanelFarmaciaService);
  private readonly medicamentoService: MedicamentoService = inject(MedicamentoService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  protected readonly etiquetaEstado: Record<EstadoProducto, string> = {
    disponible: "Disponible",
    agotado: "Agotado",
    bajo_pedido: "Bajo pedido",
  };
  protected readonly formatearFecha: (fecha: string) => string = formatearFechaColombia;
  private readonly formatoPrecio: Intl.NumberFormat = new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  });

  /**
   * 
   * lo que el usuario escribe en el filtro, y la página que está viendo (la primera es la 0).
   * recarga sube de a uno cada vez que hay que volver a pedir la lista, por ejemplo
   * después de guardar
   * 
   */
  protected readonly texto: WritableSignal<string> = signal("");
  protected readonly pagina: WritableSignal<number> = signal(0);
  private readonly recarga: WritableSignal<number> = signal(0);

  /**
   * 
   * el texto del filtro ya con la pausa de 300 milisegundos. Al cambiar el filtro se
   * vuelve a la primera página, porque la que estaba viendo puede no existir con
   * el filtro nuevo
   * 
   */
  private readonly textoBuscado: Signal<string> = toSignal(
    toObservable(this.texto).pipe(
      debounceTime(ESPERA_ESCRITURA_MS),
      map((valor) => valor.trim()),
      distinctUntilChanged(),
      tap(() => this.pagina.set(0)),
    ),
    { initialValue: "" },
  );

  private readonly consulta: Signal<{ texto: string; pagina: number; recarga: number }> = computed(() => ({
    texto: this.textoBuscado(),
    pagina: this.pagina(),
    recarga: this.recarga(),
  }));

  /**
   * 
   * el estado de la tabla. Cada vez que cambia el filtro, la página o la recarga, vuelve
   * a pedir la lista; si había una petición en camino, se cancela y solo vale la última
   * 
   */
  protected readonly estado: Signal<EstadoInventario> = toSignal(
    toObservable(this.consulta).pipe(switchMap((consulta) => this.cargar(consulta.texto, consulta.pagina))),
    { initialValue: { tipo: "cargando" } as EstadoInventario },
  );

  protected readonly aviso: WritableSignal<Aviso | null> = signal(null);

  /**
   * 
   * el formulario que está abierto: agregar desde el catálogo, editar una fila o reportar
   * un medicamento a mano. Es null mientras está cerrado
   * 
   */
  protected readonly formularioAbierto: WritableSignal<"crear" | "editar" | "manual" | null> = signal(null);
  protected readonly medicamentoElegido: WritableSignal<MedicamentoElegido | null> = signal(null);
  protected readonly textoMedicamento: WritableSignal<string> = signal("");
  protected readonly errorFormulario: WritableSignal<string | null> = signal(null);
  protected readonly guardando: WritableSignal<boolean> = signal(false);

  /**
   * 
   * los medicamentos que coinciden con lo que se escribe al agregar un producto. Usa
   * el mismo buscador de la landing; con menos de 2 letras no busca
   * 
   */
  protected readonly resultadosMedicamento: Signal<MedicamentoBusqueda[]> = toSignal(
    toObservable(this.textoMedicamento).pipe(
      debounceTime(ESPERA_ESCRITURA_MS),
      map((valor) => valor.trim()),
      distinctUntilChanged(),
      switchMap((valor) =>
        valor.length < LONGITUD_MINIMA_BUSQUEDA
          ? of<MedicamentoBusqueda[]>([])
          : this.medicamentoService.buscar(valor).pipe(catchError(() => of<MedicamentoBusqueda[]>([]))),
      ),
    ),
    { initialValue: [] as MedicamentoBusqueda[] },
  );

  protected readonly formulario = new FormGroup({
    cantidad: new FormControl<number | null>(null, [Validators.required, Validators.pattern("^[0-9]+$")]),
    precio: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
    estado: new FormControl<"" | EstadoProducto>("", { nonNullable: true }),
  });

  /**
   * 
   * las opciones de los selectores del reporte manual (HU-7). Se piden la primera vez que
   * se abre el formulario manual; si fallan, se muestra el error con un botón para reintentar
   * 
   */
  protected readonly catalogos: WritableSignal<Catalogos | null> = signal(null);
  protected readonly errorCatalogos: WritableSignal<string | null> = signal(null);
  protected readonly categorias: Signal<OpcionCatalogo[]> = computed(() => this.catalogos()?.categorias ?? []);
  protected readonly formas: Signal<OpcionCatalogo[]> = computed(() => this.catalogos()?.formasFarmaceuticas ?? []);
  protected readonly estadosManual: Signal<EstadoProducto[]> = computed(() => this.catalogos()?.estados ?? []);

  /**
   * 
   * el formulario del reporte manual. Las reglas son las mismas del backend: nombre comercial
   * de 2 a 100 caracteres, principio activo de 2 a 150, cantidad entera desde 0 y precio mayor
   * que 0 con hasta 8 enteros y 2 decimales
   * 
   */
  protected readonly formularioManual = new FormGroup({
    nombreComercial: new FormControl<string>("", { nonNullable: true, validators: [largoLimpio(2, 100)] }),
    principioActivo: new FormControl<string>("", { nonNullable: true, validators: [largoLimpio(2, 150)] }),
    categoriaId: new FormControl<number | null>(null, [Validators.required]),
    presentacion: new FormControl<string>("", { nonNullable: true, validators: [largoLimpio(1, 100)] }),
    formaFarmaceuticaId: new FormControl<number | null>(null, [Validators.required]),
    cantidad: new FormControl<number | null>(null, [Validators.required, Validators.pattern("^[0-9]+$")]),
    precio: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0.01),
      Validators.pattern("^[0-9]{1,8}([.][0-9]{1,2})?$"),
    ]),
    estado: new FormControl<"" | EstadoProducto>("", { nonNullable: true }),
  });

  /**
   * 
   * el producto que se quiere dar de baja y está esperando confirmación. Es null si
   * no hay ninguno
   * 
   */
  protected readonly productoPorDarDeBaja: WritableSignal<ProductoInventario | null> = signal(null);
  protected readonly procesandoBaja: WritableSignal<boolean> = signal(false);

  protected formatearPrecio(valor: number): string {
    return this.formatoPrecio.format(valor);
  }

  protected alBuscar(valor: string): void {
    this.texto.set(valor);
  }

  protected irAPagina(numero: number): void {
    this.pagina.set(numero);
  }

  protected cerrarAviso(): void {
    this.aviso.set(null);
  }

  protected abrirCreacion(): void {
    this.aviso.set(null);
    this.productoPorDarDeBaja.set(null);
    this.errorFormulario.set(null);
    this.medicamentoElegido.set(null);
    this.textoMedicamento.set("");
    this.formulario.reset({ cantidad: null, precio: null, estado: "" });
    this.formularioAbierto.set("crear");
  }

  /**
   * 
   * abre el formulario con los datos de la fila. El estado queda en "automático" salvo
   * que el producto esté bajo pedido: así, si se cambia la cantidad a 0, el backend
   * lo pasa a agotado solo, en vez de rechazar un "disponible" con 0 unidades
   * 
   */
  protected abrirEdicion(producto: ProductoInventario): void {
    this.aviso.set(null);
    this.productoPorDarDeBaja.set(null);
    this.errorFormulario.set(null);
    this.medicamentoElegido.set({
      id: producto.medicamentoId,
      nombreComercial: producto.nombreComercial,
      detalle: `${producto.principioActivo} · ${producto.presentacion}`,
    });
    this.formulario.reset({
      cantidad: producto.cantidadDisponible,
      precio: producto.precio,
      estado: producto.estado === "bajo_pedido" ? "bajo_pedido" : "",
    });
    this.formularioAbierto.set("editar");
  }

  /**
   * 
   * abre el formulario para reportar un medicamento escribiendo sus datos (HU-7). Las
   * opciones de los selectores se piden solo la primera vez
   * 
   */
  protected abrirManual(): void {
    this.aviso.set(null);
    this.productoPorDarDeBaja.set(null);
    this.errorFormulario.set(null);
    this.medicamentoElegido.set(null);
    this.textoMedicamento.set("");
    this.formularioManual.reset({
      nombreComercial: "",
      principioActivo: "",
      categoriaId: null,
      presentacion: "",
      formaFarmaceuticaId: null,
      cantidad: null,
      precio: null,
      estado: "",
    });
    this.formularioAbierto.set("manual");
    if (!this.catalogos()) {
      this.cargarCatalogos();
    }
  }

  protected cargarCatalogos(): void {
    this.errorCatalogos.set(null);
    this.panel
      .obtenerCatalogos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (catalogos: Catalogos) => this.catalogos.set(catalogos),
        error: (error: unknown) => this.errorCatalogos.set(mensajeDeError(error)),
      });
  }

  protected cerrarFormulario(): void {
    this.formularioAbierto.set(null);
    this.medicamentoElegido.set(null);
    this.errorFormulario.set(null);
  }

  protected elegirMedicamento(medicamento: MedicamentoBusqueda): void {
    this.medicamentoElegido.set({
      id: medicamento.id,
      nombreComercial: medicamento.nombreComercial,
      detalle: `${medicamento.principioActivo} · ${medicamento.presentacion} · ${medicamento.formaFarmaceutica}`,
    });
    this.textoMedicamento.set("");
    this.errorFormulario.set(null);
  }

  protected cambiarMedicamento(): void {
    this.medicamentoElegido.set(null);
  }

  protected guardar(): void {
    const elegido: MedicamentoElegido | null = this.medicamentoElegido();
    if (!elegido) {
      this.errorFormulario.set("Elige un medicamento");
      return;
    }
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();
    const datos: GuardarProducto = {
      medicamentoId: elegido.id,
      cantidad: valores.cantidad ?? 0,
      precio: valores.precio ?? 0,
      estado: valores.estado === "" ? null : valores.estado,
    };

    this.errorFormulario.set(null);
    this.guardando.set(true);
    this.panel
      .guardarProducto(datos)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (producto) => {
          this.guardando.set(false);
          this.cerrarFormulario();
          this.aviso.set({ tipo: "ok", texto: `Se guardó ${producto.nombreComercial}` });
          this.recarga.update((n) => n + 1);
        },
        error: (error: unknown) => {
          this.guardando.set(false);
          this.errorFormulario.set(mensajeDeError(error));
        },
      });
  }

  protected guardarManual(): void {
    if (this.formularioManual.invalid) {
      this.formularioManual.markAllAsTouched();
      return;
    }

    const valores = this.formularioManual.getRawValue();
    const datos: GuardarProductoManual = {
      nombreComercial: limpiarTexto(valores.nombreComercial),
      principioActivo: limpiarTexto(valores.principioActivo),
      categoriaId: valores.categoriaId ?? 0,
      presentacion: limpiarTexto(valores.presentacion),
      formaFarmaceuticaId: valores.formaFarmaceuticaId ?? 0,
      cantidad: valores.cantidad ?? 0,
      precio: valores.precio ?? 0,
      estado: valores.estado === "" ? null : valores.estado,
    };

    this.errorFormulario.set(null);
    this.guardando.set(true);
    this.panel
      .guardarProductoManual(datos)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (producto: ProductoInventario) => {
          this.guardando.set(false);
          this.cerrarFormulario();
          this.aviso.set({ tipo: "ok", texto: `Se guardó ${producto.nombreComercial}` });
          this.recarga.update((n) => n + 1);
        },
        error: (error: unknown) => {
          this.guardando.set(false);
          this.errorFormulario.set(mensajeDeError(error));
        },
      });
  }

  protected pedirBaja(producto: ProductoInventario): void {
    this.aviso.set(null);
    this.productoPorDarDeBaja.set(producto);
  }

  protected cancelarBaja(): void {
    this.productoPorDarDeBaja.set(null);
  }

  protected confirmarBaja(): void {
    const producto: ProductoInventario | null = this.productoPorDarDeBaja();
    if (!producto) {
      return;
    }

    this.procesandoBaja.set(true);
    this.panel
      .marcarAgotado(producto.medicamentoId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.procesandoBaja.set(false);
          this.productoPorDarDeBaja.set(null);
          this.aviso.set({ tipo: "ok", texto: `${producto.nombreComercial} quedó como agotado` });
          this.recarga.update((n) => n + 1);
        },
        error: (error: unknown) => {
          this.procesandoBaja.set(false);
          this.productoPorDarDeBaja.set(null);
          this.aviso.set({ tipo: "error", texto: mensajeDeError(error) });
        },
      });
  }

  /**
   * 
   * pide una página del inventario y devuelve los estados por los que pasa: cargando,
   * y después listo con la página o error con el mensaje
   * 
   */
  private cargar(texto: string, pagina: number): Observable<EstadoInventario> {
    return this.panel.listarInventario(texto, pagina, TAMANO_PAGINA).pipe(
      map((resultado): EstadoInventario => ({ tipo: "listo", resultado })),
      catchError((error: unknown) => of<EstadoInventario>({ tipo: "error", mensaje: mensajeDeError(error) })),
      startWith<EstadoInventario>({ tipo: "cargando" }),
    );
  }
}