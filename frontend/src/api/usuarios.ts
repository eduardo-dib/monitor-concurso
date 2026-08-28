import api from './api'
import type { UsuarioResponse, LoginResponse } from '@/types/api'

export interface CadastroPayload {
  nome: string
  email: string
  senha: string
}

export async function cadastrarUsuario(payload: CadastroPayload): Promise<UsuarioResponse> {
  const { data } = await api.post<UsuarioResponse>('/usuarios/cadastrar', payload)
  return data
}

export interface LoginPayload {
  email: string
  senha: string
}

export async function login(payload: LoginPayload): Promise<LoginResponse> {
  const { data } = await api.post<LoginResponse>('/usuarios/login', payload)
  return data
}

export async function buscarUsuarioLogado(): Promise<UsuarioResponse> {
  const { data } = await api.get<UsuarioResponse>('/usuarios/me')
  return data
}
