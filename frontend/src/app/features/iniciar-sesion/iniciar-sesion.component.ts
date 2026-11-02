import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  WritableSignal,
  inject,
  signal,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";

import {
  RolUsuario,
  Sesion,
  SesionService,
  mensajeDeSesionError,
  rutaInicio,
} from "../../core/services/sesion.service";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";

/**
 *
 * identificación: la cédula del paciente/cuidador (6 a 10 dígitos) o el UUID
 * que se le dio a la farmacia al registrarse
 *
 */
const PATRON_IDENTIFICACION: RegExp =
  /^(?:\d{6,10}|[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})$/;

/**
 *
 * la página de inicio de sesión: identificación, contraseña y, si todo está
 * bien, el usuario entra a su panel
 *
 */
@Component({
  selector: "app-iniciar-sesion",
  imports: [ReactiveFormsModule, RouterLink, BotonTemaComponent],
  templateUrl: "./iniciar-sesion.component.html",
  styleUrl: "./iniciar-sesion.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IniciarSesionComponent {
  private readonly sesionService: SesionService = inject(SesionService);
  private readonly router: Router = inject(Router);
  private readonly ruta: ActivatedRoute = inject(ActivatedRoute);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  protected readonly enviando: WritableSignal<boolean> = signal(false);
  protected readonly error: WritableSignal<string | null> = signal(null);

  protected readonly formulario = new FormGroup({
    identificacion: new FormControl("", {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(PATRON_IDENTIFICACION)],
    }),
    contrasena: new FormControl("", {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(8), Validators.maxLength(25)],
    }),
  });

  /**
   *
   * valida y pide la sesión. Si el backend responde bien, entra al panel del rol
   * o vuelve a la vista que el guard había pedido
   *
   */
  protected enviar(): void {
    this.error.set(null);

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();
    this.enviando.set(true);
    this.sesionService
      .iniciarSesion(valores.identificacion.trim(), valores.contrasena)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (sesion: Sesion) => {
          this.enviando.set(false);
          void this.router.navigateByUrl(this.destino(sesion.rol));
        },
        error: (error: unknown) => {
          this.enviando.set(false);
          this.error.set(mensajeDeSesionError(error));
        },
      });
  }

  /**
   *
   * a dónde ir: a la vista que el usuario quería abrir si es una ruta interna,
   * o al panel de su rol
   *
   */
  private destino(rol: RolUsuario): string {
    const volver: string | null = this.ruta.snapshot.queryParamMap.get("volver");
    if (volver && volver.startsWith("/") && !volver.startsWith("//")) {
      return volver;
    }
    return rutaInicio(rol);
  }
}
