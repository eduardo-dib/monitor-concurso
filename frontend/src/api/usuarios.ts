import api from './api'
import type { CadastroResponse } from '@/types/api'

export interface CadastroPayload {
  nome: string
  email: string
  senha: string
}

export async function cadastrarUsuario(payload: CadastroPayload): Promise<CadastroResponse> {
  const { data } = await api.post<CadastroResponse>('/usuarios/cadastrar', payload)
  return data
}
