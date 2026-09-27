/** Mirrors the backend MedicamentoBusquedaDto returned by the public medicines search */
export interface MedicamentoBusqueda {
  id: string;
  nombreComercial: string;
  principioActivo: string;
  presentacion: string;
  formaFarmaceutica: string;
}
