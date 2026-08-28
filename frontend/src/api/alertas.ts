import api from './api'
import type { AlertaResponse } from '@/types/api'

export async function listarAlertas(): Promise<AlertaResponse[]> {
  const { data } = await api.get<AlertaResponse[]>('/alertas')
  return data
}

export async function deletarAlerta(id: number): Promise<void> {
  await api.delete(`/alertas/${id}`)
}
