import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  WritableSignal,
  inject,
  signal,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
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
  DatosFarmacia,
  GuardarDatosFarmacia,
  PanelFarmaciaService,
  formatearFechaColombia,
  mensajeDeError,
} from "../../../core/services/panel-farmacia.service";

/**
 * Los estados de la pantalla, para saber qué mostrar.
 */
type EstadoDatos =
  | { tipo: "cargando" }
  | { tipo: "listo"; datos: DatosFarmacia }
  | { tipo: "error"; mensaje: string };

/**
 * Los tipos de vía más comunes, solo como sugerencia: el campo acepta cualquier texto.
 */
const TIPOS_VIA: string[] = [
  "Calle",
  "Carrera",
  "Avenida",
  "Circular",
  "Diagonal",
  "Transversal",
  "Autopista",
  "Vía",
];

/**
 * Un campo obligatorio no puede quedar solo con espacios; el backend también lo rechaza.
 */
const obligatorio: ValidatorFn = (control: AbstractControl): ValidationErrors | null =>
  typeof control.value === "string" && control.value.trim().length > 0 ? null : { required: true };

/**
 * Latitud y longitud van juntas o ninguna: con una sola, el marcador quedaría a medio mover.
 */
const coordenadasJuntas: ValidatorFn = (grupo: AbstractControl): ValidationErrors | null => {
  const latitud: unknown = grupo.get("latitud")?.value;
  const longitud: unknown = grupo.get("longitud")?.value;
  return (latitud === null) !== (longitud === null) ? { coordenadasIncompletas: true } : null;
};

/**
 * Vacío y solo espacios se envían como null, igual que los guarda el backend.
 */
function vacioANull(valor: string): string | null {
  const limpio: string = valor.trim();
  return limpio.length === 0 ? null : limpio;
}

/**
 * "Mi farmacia": los datos que la farmacia puede editar (nombre, dirección por partes,
 * horario y ubicación). Los datos legales no se muestran a propósito: los revisa un
 * administrador y no se cambian desde aquí.
 *
 * La dirección completa la arma el backend; aquí solo se muestra la que quedó guardada.
 */
@Component({
  selector: "app-datos-farmacia",
  imports: [ReactiveFormsModule],
  templateUrl: "./datos-farmacia.component.html",
  styleUrl: "./datos-farmacia.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DatosFarmaciaComponent {
  private readonly panel: PanelFarmaciaService = inject(PanelFarmaciaService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  protected readonly tiposVia: string[] = TIPOS_VIA;
  protected readonly formatearFecha: (fecha: string) => string = formatearFechaColombia;

  protected readonly estado: WritableSignal<EstadoDatos> = signal({ tipo: "cargando" });
  protected readonly guardando: WritableSignal<boolean> = signal(false);
  protected readonly errorFormulario: WritableSignal<string | null> = signal(null);
  protected readonly avisoGuardado: WritableSignal<boolean> = signal(false);

  protected readonly formulario = new FormGroup(
    {
      nombreFarmacia: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(150)],
      }),
      tipoViaPrincipal: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(20)],
      }),
      numeroViaPrincipal: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(10)],
      }),
      letraViaPrincipal: new FormControl("", { nonNullable: true, validators: [Validators.maxLength(5)] }),
      numeroViaGeneradora: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(10)],
      }),
      letraViaGeneradora: new FormControl("", { nonNullable: true, validators: [Validators.maxLength(5)] }),
      numeroPredio: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(10)],
      }),
      complemento: new FormControl("", { nonNullable: true, validators: [Validators.maxLength(100)] }),
      horarioAtencion: new FormControl("", { nonNullable: true, validators: [Validators.maxLength(100)] }),
      latitud: new FormControl<number | null>(null, [Validators.min(-90), Validators.max(90)]),
      longitud: new FormControl<number | null>(null, [Validators.min(-180), Validators.max(180)]),
    },
    { validators: [coordenadasJuntas] },
  );

  constructor() {
    this.cargar();
  }

  protected cargar(): void {
    this.estado.set({ tipo: "cargando" });
    this.panel
      .obtenerDatos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (datos) => {
          this.llenarFormulario(datos);
          this.estado.set({ tipo: "listo", datos });
        },
        error: (error: unknown) => this.estado.set({ tipo: "error", mensaje: mensajeDeError(error) }),
      });
  }

  /**
   * Vuelve el formulario a lo último que se guardó.
   */
  protected descartar(): void {
    const actual: EstadoDatos = this.estado();
    if (actual.tipo === "listo") {
      this.llenarFormulario(actual.datos);
    }
  }

  protected guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();
    const datos: GuardarDatosFarmacia = {
      nombreFarmacia: valores.nombreFarmacia.trim(),
      tipoViaPrincipal: valores.tipoViaPrincipal.trim(),
      numeroViaPrincipal: valores.numeroViaPrincipal.trim(),
      letraViaPrincipal: vacioANull(valores.letraViaPrincipal),
      numeroViaGeneradora: valores.numeroViaGeneradora.trim(),
      letraViaGeneradora: vacioANull(valores.letraViaGeneradora),
      numeroPredio: valores.numeroPredio.trim(),
      complemento: vacioANull(valores.complemento),
      horarioAtencion: vacioANull(valores.horarioAtencion),
      latitud: valores.latitud,
      longitud: valores.longitud,
    };

    this.errorFormulario.set(null);
    this.avisoGuardado.set(false);
    this.guardando.set(true);
    this.panel
      .guardarDatos(datos)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (guardados) => {
          this.guardando.set(false);
          // Se rellena con la respuesta: el backend pasa las letras a mayúscula.
          this.llenarFormulario(guardados);
          this.estado.set({ tipo: "listo", datos: guardados });
          this.avisoGuardado.set(true);
        },
        error: (error: unknown) => {
          this.guardando.set(false);
          this.errorFormulario.set(mensajeDeError(error));
        },
      });
  }

  protected cerrarAviso(): void {
    this.avisoGuardado.set(false);
  }

  private llenarFormulario(datos: DatosFarmacia): void {
    this.formulario.reset({
      nombreFarmacia: datos.nombreFarmacia,
      tipoViaPrincipal: datos.tipoViaPrincipal,
      numeroViaPrincipal: datos.numeroViaPrincipal,
      letraViaPrincipal: datos.letraViaPrincipal ?? "",
      numeroViaGeneradora: datos.numeroViaGeneradora,
      letraViaGeneradora: datos.letraViaGeneradora ?? "",
      numeroPredio: datos.numeroPredio,
      complemento: datos.complemento ?? "",
      horarioAtencion: datos.horarioAtencion ?? "",
      latitud: datos.latitud,
      longitud: datos.longitud,
    });
    this.errorFormulario.set(null);
  }
}