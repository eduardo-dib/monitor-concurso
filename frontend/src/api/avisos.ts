import api from './api'
import type { AvisoResponse } from '@/types/api'

export async function listarAvisos(): Promise<AvisoResponse[]> {
  const { data } = await api.get<AvisoResponse[]>('/avisos')
  return data
}
