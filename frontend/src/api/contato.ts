import api from './api'
import type { ContatoRequest } from '@/types/api'

export async function enviarContato(payload: ContatoRequest): Promise<string> {
  const { data } = await api.post<string>('/contato', payload)
  return data
}
