import { DOCUMENT } from "@angular/common";
import {
  ApplicationRef,
  Injectable,
  Injector,
  Signal,
  WritableSignal,
  computed,
  effect,
  inject,
  signal,
} from "@angular/core";

export type Tema = "claro" | "oscuro";

const CLAVE_TEMA: string = "medlab-tema";
const CLAVE_TEMA_MAPA: string = "medlab-tema-mapa";
const CONSULTA_OSCURO: string = "(prefers-color-scheme: dark)";
const CONSULTA_MENOS_MOVIMIENTO: string = "(prefers-reduced-motion: reduce)";

@Injectable({ providedIn: "root" })
export class TemaService {
  private readonly documento: Document = inject(DOCUMENT);
  private readonly injector: Injector = inject(Injector);
  private readonly aplicacion: ApplicationRef = inject(ApplicationRef);
  private readonly consultaSistema: MediaQueryList | undefined =
    this.documento.defaultView?.matchMedia(CONSULTA_OSCURO);

  private readonly temaSistema: WritableSignal<Tema> = signal(
    this.consultaSistema?.matches ? "oscuro" : "claro",
  );
  private readonly temaElegido: WritableSignal<Tema | null> = signal(
    this.leerGuardado(CLAVE_TEMA),
  );
  private readonly temaMapaElegido: WritableSignal<Tema | null> = signal(
    this.leerGuardado(CLAVE_TEMA_MAPA),
  );

  readonly tema: Signal<Tema> = computed(
    () => this.temaElegido() ?? this.temaSistema(),
  );
  readonly temaMapa: Signal<Tema> = computed(
    () => this.temaMapaElegido() ?? this.tema(),
  );

  iniciar(): void {
    this.consultaSistema?.addEventListener(
      "change",
      (evento: MediaQueryListEvent) =>
        this.temaSistema.set(evento.matches ? "oscuro" : "claro"),
    );

    effect(
      () => {
        this.documento.documentElement.dataset["tema"] = this.tema();
      },
      { injector: this.injector },
    );
  }

  alternar(): void {
    const nuevo: Tema = this.tema() === "oscuro" ? "claro" : "oscuro";
    this.conTransicion(() => this.temaElegido.set(nuevo));
    this.guardar(CLAVE_TEMA, nuevo);
  }

  alternarMapa(): void {
    const nuevo: Tema = this.temaMapa() === "oscuro" ? "claro" : "oscuro";
    this.conTransicion(() => this.temaMapaElegido.set(nuevo));
    this.guardar(CLAVE_TEMA_MAPA, nuevo);
  }

  private conTransicion(cambio: () => void): void {
    const menosMovimiento: boolean =
      this.documento.defaultView?.matchMedia(CONSULTA_MENOS_MOVIMIENTO).matches ?? true;

    if (!this.documento.startViewTransition || menosMovimiento) {
      cambio();
      return;
    }

    this.documento.startViewTransition(() => {
      cambio();
      this.aplicacion.tick();
    });
  }

  private guardar(clave: string, tema: Tema): void {
    try {
      localStorage.setItem(clave, tema);
    } catch {
      return;
    }
  }

  private leerGuardado(clave: string): Tema | null {
    try {
      const guardado: string | null = localStorage.getItem(clave);
      return guardado === "claro" || guardado === "oscuro" ? guardado : null;
    } catch {
      return null;
    }
  }
}
