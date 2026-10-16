import { DOCUMENT } from "@angular/common";
import {
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
const CONSULTA_OSCURO: string = "(prefers-color-scheme: dark)";

@Injectable({ providedIn: "root" })
export class TemaService {
  private readonly documento: Document = inject(DOCUMENT);
  private readonly injector: Injector = inject(Injector);
  private readonly consultaSistema: MediaQueryList | undefined =
    this.documento.defaultView?.matchMedia(CONSULTA_OSCURO);

  private readonly temaSistema: WritableSignal<Tema> = signal(
    this.consultaSistema?.matches ? "oscuro" : "claro",
  );
  private readonly temaElegido: WritableSignal<Tema | null> = signal(
    this.leerGuardado(),
  );

  readonly tema: Signal<Tema> = computed(
    () => this.temaElegido() ?? this.temaSistema(),
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
    this.temaElegido.set(nuevo);

    try {
      localStorage.setItem(CLAVE_TEMA, nuevo);
    } catch {
      return;
    }
  }

  private leerGuardado(): Tema | null {
    try {
      const guardado: string | null = localStorage.getItem(CLAVE_TEMA);
      return guardado === "claro" || guardado === "oscuro" ? guardado : null;
    } catch {
      return null;
    }
  }
}
