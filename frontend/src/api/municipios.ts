// src/api/municipios.ts
import api from './api'

export interface MunicipioOption {
  territory_id: string
  territory_name: string
  state_code: string
}

export async function listarMunicipios(): Promise<MunicipioOption[]> {
  const { data } = await api.get<MunicipioOption[]>('/municipios')
  return data
}
