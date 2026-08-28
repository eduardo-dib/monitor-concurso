import api from './api'
import type { AlertaResponse, CriarAlertaPayload } from '@/types/api'

export async function listarAlertas(): Promise<AlertaResponse[]> {
  const { data } = await api.get<AlertaResponse[]>('/alertas')
  return data
}

export async function deletarAlerta(id: number): Promise<void> {
  await api.delete(`/alertas/${id}`)
}


export async function criarAlerta(payload: CriarAlertaPayload): Promise<AlertaResponse> {
  const { data } = await api.post<AlertaResponse>('/alertas', payload)
  return data
}
