import { ChangeDetectionStrategy, Component } from "@angular/core";

/**
 *
 * la forma de cada paso de "Cómo funciona"
 *
 */
interface Paso {
  /**
   *
   * el nombre corto del paso
   *
   */
  titulo: string;

  /**
   *
   * el texto que explica el paso
   *
   */
  descripcion: string;

  /**
   *
   * el dibujo del ícono, escrito como el trazo de un SVG
   *
   */
  icono: string;
}

/**
 *
 * la sección de la landing que explica en tres pasos cómo se usa MedLab
 *
 */
@Component({
  selector: "app-como-funciona",
  templateUrl: "./como-funciona.component.html",
  styleUrl: "./como-funciona.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComoFuncionaComponent {
  /**
   *
   * los tres pasos, en el orden en que se muestran
   *
   */
  protected readonly pasos: readonly Paso[] = [
    {
      titulo: "Busca tu medicamento",
      descripcion:
        "Escribe el nombre comercial o el principio activo. No necesitas crear una cuenta.",
      icono: "M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14z M20 20l-4-4",
    },
    {
      titulo: "Mira quién lo tiene",
      descripcion:
        "Revisa en el mapa las farmacias aprobadas cercanas que reportan disponibilidad.",
      icono:
        "M12 21s-7-6.2-7-11a7 7 0 0 1 14 0c0 4.8-7 11-7 11z M12 12.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z",
    },
    {
      titulo: "Ayuda a otros",
      descripcion:
        "Califica si la información era correcta para que la comunidad pueda confiar en ella.",
      icono: "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z M8 12l3 3 5-6",
    },
  ];
}