import { ChangeDetectionStrategy, Component } from "@angular/core";
import { RouterLink } from "@angular/router";

/**
 *
 * la forma de cada beneficio que se le ofrece a una farmacia
 *
 */
interface Beneficio {
  /**
   *
   * lo que se destaca en negrilla, por ejemplo "Visibilidad gratuita"
   *
   */
  titulo: string;

  /**
   *
   * la frase que completa el beneficio
   *
   */
  descripcion: string;
}

/**
 *
 * un medicamento de la vista de ejemplo del panel de inventario
 *
 */
interface MedicamentoEjemplo {
  /**
   *
   * el nombre que se muestra en la fila
   *
   */
  nombre: string;

  /**
   *
   * si en el ejemplo está disponible o agotado
   *
   */
  disponible: boolean;
}

/**
 *
 * la sección de la landing dirigida a las farmacias: explica por qué conviene
 * aliarse con MedLab e invita a registrarse
 *
 * la vista del panel es solo una ilustración de ejemplo, no usa datos reales
 *
 */
@Component({
  selector: "app-para-farmacias",
  imports: [RouterLink],
  templateUrl: "./para-farmacias.component.html",
  styleUrl: "./para-farmacias.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ParaFarmaciasComponent {
  /**
   *
   * los beneficios de ser una farmacia aliada
   *
   */
  protected readonly beneficios: readonly Beneficio[] = [
    {
      titulo: "Visibilidad gratuita en el mapa.",
      descripcion: "Aparece ante quienes buscan un medicamento cerca de ti.",
    },
    {
      titulo: "Un panel para tu inventario.",
      descripcion: "Actualiza qué medicamentos tienes disponibles cuando quieras.",
    },
    {
      titulo: "Confianza que se nota.",
      descripcion:
        "Las calificaciones de la comunidad muestran qué tan al día está tu información.",
    },
    {
      titulo: "Solo farmacias verificadas.",
      descripcion: "Revisamos cada farmacia antes de mostrarla en el mapa.",
    },
  ];

  /**
   *
   * los medicamentos de la vista de ejemplo del panel
   *
   */
  protected readonly ejemplo: readonly MedicamentoEjemplo[] = [
    { nombre: "Acetaminofén 500 mg", disponible: true },
    { nombre: "Ibuprofeno 400 mg", disponible: true },
    { nombre: "Loratadina 10 mg", disponible: false },
    { nombre: "Omeprazol 20 mg", disponible: true },
  ];
}