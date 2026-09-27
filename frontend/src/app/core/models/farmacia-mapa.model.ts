/** Mirrors the backend FarmaciaMapaDto returned by the public pharmacies endpoint */
export interface FarmaciaMapa {
  id: string;
  nombre: string;
  latitud: number;
  longitud: number;
}
