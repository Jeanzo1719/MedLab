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
import { RouterLink } from "@angular/router";
import { Observable } from "rxjs";

import {
  RegistroCreado,
  RegistroFarmacia,
  RegistroPaciente,
  RegistroService,
  RolRegistro,
  mensajeDeRegistroError,
} from "../../core/services/registro.service";
import { Coordenadas, UbicacionService } from "../../core/services/ubicacion.service";
import { MapaComponent } from "../../shared/components/mapa/mapa.component";
import { BotonTemaComponent } from "../../shared/components/boton-tema/boton-tema.component";

/**
 *
 * los tipos de vía más comunes, solo como sugerencia: el campo acepta cualquier
 * texto
 *
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
 *
 * un correo con exactamente una @, sin espacios, con la parte anterior limitada
 * a letras, números y . _ - +, y la posterior con formato nombre.extensión
 * (extensión de 2 a 10 letras)
 *
 */
const PATRON_CORREO: RegExp =
  /^[A-Za-z0-9._+-]+@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\.)+[A-Za-z]{2,10}$/;

/**
 *
 * entre 8 y 25 caracteres, con al menos una mayúscula, una minúscula y un
 * símbolo (cualquier cosa que no sea letra ni número)
 *
 */
const PATRON_CONTRASENA: RegExp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,25}$/;

/**
 *
 * teléfono de 7 a 20 caracteres: números, espacios y los signos + ( ) -
 *
 */
const PATRON_TELEFONO: RegExp = /^[0-9+() -]{7,20}$/;

/**
 *
 * matrícula mercantil: solo letras y números, entre 5 y 20 caracteres
 *
 */
const PATRON_MATRICULA: RegExp = /^[A-Za-z0-9]{5,20}$/;

/**
 *
 * cédula: solo dígitos, entre 6 y 10
 *
 */
const PATRON_IDENTIFICACION: RegExp = /^\d{6,10}$/;

/**
 *
 * un campo obligatorio no puede quedar solo con espacios; el backend también lo
 * rechaza
 *
 */
const obligatorio: ValidatorFn = (control: AbstractControl): ValidationErrors | null =>
  typeof control.value === "string" && control.value.trim().length > 0 ? null : { required: true };

/**
 *
 * las dos contraseñas tienen que ser iguales; si no, el grupo del formulario
 * queda inválido y se muestra el aviso junto al campo de confirmación
 *
 */
const contrasenasIguales: ValidatorFn = (grupo: AbstractControl): ValidationErrors | null => {
  const contrasena: unknown = grupo.get("contrasena")?.value;
  const confirmacion: unknown = grupo.get("confirmarContrasena")?.value;
  return contrasena === confirmacion ? null : { contrasenasDistintas: true };
};

/**
 *
 * el resultado del registro, para cambiar el formulario por el mensaje final
 *
 */
interface ExitoRegistro {
  rol: RolRegistro;
  estado: string;
}

/**
 *
 * la página de registro. Primero pregunta el rol y después muestra solo los
 * campos que ese rol necesita: el paciente deja sus datos personales y la
 * farmacia sus datos legales y su ubicación en el mapa
 *
 */
@Component({
  selector: "app-registro",
  imports: [ReactiveFormsModule, RouterLink, MapaComponent, BotonTemaComponent],
  templateUrl: "./registro.component.html",
  styleUrl: "./registro.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegistroComponent {
  private readonly registroService: RegistroService = inject(RegistroService);
  private readonly ubicacionService: UbicacionService = inject(UbicacionService);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);

  protected readonly tiposVia: string[] = TIPOS_VIA;

  protected readonly rol: WritableSignal<RolRegistro> = signal<RolRegistro>("paciente");
  protected readonly enviando: WritableSignal<boolean> = signal(false);
  protected readonly errorServidor: WritableSignal<string | null> = signal(null);
  protected readonly exito: WritableSignal<ExitoRegistro | null> = signal(null);

  protected readonly coordenadas: WritableSignal<Coordenadas | null> = signal(null);
  protected readonly buscandoUbicacion: WritableSignal<boolean> = signal(false);
  protected readonly errorUbicacion: WritableSignal<string | null> = signal(null);
  protected readonly errorMapa: WritableSignal<string | null> = signal(null);

  protected readonly formularioPaciente = new FormGroup(
    {
      identificacion: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.pattern(PATRON_IDENTIFICACION)],
      }),
      primerNombre: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(50)],
      }),
      segundoNombre: new FormControl("", {
        nonNullable: true,
        validators: [Validators.maxLength(100)],
      }),
      apellidos: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(100)],
      }),
      correo: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(150), Validators.pattern(PATRON_CORREO)],
      }),
      telefono: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.pattern(PATRON_TELEFONO)],
      }),
      epsAfiliada: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(100)],
      }),
      contrasena: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.pattern(PATRON_CONTRASENA)],
      }),
      confirmarContrasena: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio],
      }),
    },
    { validators: [contrasenasIguales] },
  );

  protected readonly formularioFarmacia = new FormGroup(
    {
      nombreFarmacia: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(150)],
      }),
      nit: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(20)],
      }),
      rut: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(20)],
      }),
      matriculaMercantil: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.pattern(PATRON_MATRICULA)],
      }),
      identificacionDirectorTecnico: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.pattern(PATRON_IDENTIFICACION)],
      }),
      licenciaSanitaria: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(50)],
      }),
      tipoViaPrincipal: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(20)],
      }),
      numeroViaPrincipal: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(10)],
      }),
      letraViaPrincipal: new FormControl("", {
        nonNullable: true,
        validators: [Validators.maxLength(5)],
      }),
      numeroViaGeneradora: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(10)],
      }),
      letraViaGeneradora: new FormControl("", {
        nonNullable: true,
        validators: [Validators.maxLength(5)],
      }),
      numeroPredio: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(10)],
      }),
      complemento: new FormControl("", {
        nonNullable: true,
        validators: [Validators.maxLength(100)],
      }),
      correo: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(150), Validators.pattern(PATRON_CORREO)],
      }),
      telefono: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.pattern(PATRON_TELEFONO)],
      }),
      epsAfiliada: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.maxLength(100)],
      }),
      contrasena: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio, Validators.pattern(PATRON_CONTRASENA)],
      }),
      confirmarContrasena: new FormControl("", {
        nonNullable: true,
        validators: [obligatorio],
      }),
    },
    { validators: [contrasenasIguales] },
  );

  /**
   *
   * cambia el formulario que se muestra. Los dos formularios se conservan para
   * que el usuario no pierda lo que ya escribió si se equivoca de rol
   *
   */
  protected cambiarRol(rol: RolRegistro): void {
    this.rol.set(rol);
    this.errorServidor.set(null);
  }

  /**
   *
   * muestra las coordenadas con seis decimales, que es la precisión que guarda
   * la base de datos
   *
   */
  protected formatearCoordenada(valor: number): string {
    return valor.toFixed(6);
  }

  /**
   *
   * guarda el punto que el usuario tocó en el mapa
   *
   */
  protected elegirUbicacion(coordenadas: Coordenadas): void {
    this.coordenadas.set(coordenadas);
    this.errorMapa.set(null);
    this.errorUbicacion.set(null);
  }

  /**
   *
   * arma la dirección con las partes del formulario y le pide a Nominatim sus
   * coordenadas. Si no la encuentra, el usuario puede tocar el mapa a mano
   *
   */
  protected ubicarSegunDireccion(): void {
    const valores = this.formularioFarmacia.getRawValue();
    const tipoVia: string = valores.tipoViaPrincipal.trim();
    const numeroVia: string = valores.numeroViaPrincipal.trim();
    const numeroGeneradora: string = valores.numeroViaGeneradora.trim();
    const numeroPredio: string = valores.numeroPredio.trim();

    if (!tipoVia || !numeroVia || !numeroGeneradora || !numeroPredio) {
      this.errorUbicacion.set(
        "Escribe primero el tipo de vía, el número, la vía generadora y el predio.",
      );
      return;
    }

    const letraVia: string = valores.letraViaPrincipal.trim();
    const letraGeneradora: string = valores.letraViaGeneradora.trim();
    const complemento: string = valores.complemento.trim();
    const direccion: string = [
      `${tipoVia} ${numeroVia}${letraVia} # ${numeroGeneradora}${letraGeneradora} - ${numeroPredio}`,
      complemento,
      "Medellín, Antioquia, Colombia",
    ]
      .filter((parte: string): boolean => parte.length > 0)
      .join(", ");

    this.buscandoUbicacion.set(true);
    this.errorUbicacion.set(null);
    this.ubicacionService
      .buscarDireccion(direccion)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (encontrada: Coordenadas | null) => {
          this.buscandoUbicacion.set(false);
          if (!encontrada) {
            this.errorUbicacion.set(
              "No encontramos esa dirección. Acerca el mapa y toca el punto exacto.",
            );
            return;
          }
          this.elegirUbicacion(encontrada);
        },
        error: () => {
          this.buscandoUbicacion.set(false);
          this.errorUbicacion.set(
            "No pudimos buscar la dirección. Toca el punto exacto en el mapa.",
          );
        },
      });
  }

  /**
   *
   * envía el formulario del rol elegido. Si es farmacia, exige que la ubicación
   * esté marcada antes de llamar al backend
   *
   */
  protected enviar(): void {
    this.errorServidor.set(null);

    if (this.rol() === "paciente") {
      this.enviarPaciente();
      return;
    }

    const ubicacion: Coordenadas | null = this.coordenadas();
    if (!ubicacion) {
      this.formularioFarmacia.markAllAsTouched();
      this.errorMapa.set("Marca la ubicación de la farmacia en el mapa.");
      return;
    }

    if (this.formularioFarmacia.invalid) {
      this.formularioFarmacia.markAllAsTouched();
      return;
    }

    const valores = this.formularioFarmacia.getRawValue();
    const datos: RegistroFarmacia = {
      correo: valores.correo.trim(),
      contrasena: valores.contrasena,
      telefono: valores.telefono.trim(),
      epsAfiliada: valores.epsAfiliada.trim(),
      nombreFarmacia: valores.nombreFarmacia.trim(),
      tipoViaPrincipal: valores.tipoViaPrincipal.trim(),
      numeroViaPrincipal: valores.numeroViaPrincipal.trim(),
      letraViaPrincipal: vacioANull(valores.letraViaPrincipal),
      numeroViaGeneradora: valores.numeroViaGeneradora.trim(),
      letraViaGeneradora: vacioANull(valores.letraViaGeneradora),
      numeroPredio: valores.numeroPredio.trim(),
      complemento: vacioANull(valores.complemento),
      nit: valores.nit.trim(),
      rut: valores.rut.trim(),
      matriculaMercantil: valores.matriculaMercantil.trim(),
      identificacionDirectorTecnico: valores.identificacionDirectorTecnico.trim(),
      licenciaSanitaria: valores.licenciaSanitaria.trim(),
      latitud: ubicacion.latitud,
      longitud: ubicacion.longitud,
    };

    this.enviarRegistro(
      (): Observable<RegistroCreado> => this.registroService.registrarFarmacia(datos),
      "farmacia",
    );
  }

  private enviarPaciente(): void {
    if (this.formularioPaciente.invalid) {
      this.formularioPaciente.markAllAsTouched();
      return;
    }

    const valores = this.formularioPaciente.getRawValue();
    const datos: RegistroPaciente = {
      identificacion: valores.identificacion.trim(),
      correo: valores.correo.trim(),
      contrasena: valores.contrasena,
      telefono: valores.telefono.trim(),
      epsAfiliada: valores.epsAfiliada.trim(),
      primerNombre: valores.primerNombre.trim(),
      // Si la persona tiene tres o más nombres, todos los adicionales caben aquí.
      segundoNombre: vacioANull(valores.segundoNombre),
      apellidos: valores.apellidos.trim(),
    };

    this.enviarRegistro(
      (): Observable<RegistroCreado> => this.registroService.registrarPaciente(datos),
      "paciente",
    );
  }

  /**
   *
   * comparte el envío y el manejo de la respuesta entre los dos formularios
   *
   */
  private enviarRegistro(
    peticion: () => Observable<RegistroCreado>,
    rol: RolRegistro,
  ): void {
    this.enviando.set(true);
    peticion()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (creado: RegistroCreado) => {
          this.enviando.set(false);
          this.exito.set({ rol, estado: creado.estado });
        },
        error: (error: unknown) => {
          this.enviando.set(false);
          this.errorServidor.set(mensajeDeRegistroError(error));
        },
      });
  }
}

/**
 *
 * vacío y solo espacios se envían como null, igual que los guarda el backend
 *
 */
function vacioANull(valor: string): string | null {
  const limpio: string = valor.trim();
  return limpio.length === 0 ? null : limpio;
}
