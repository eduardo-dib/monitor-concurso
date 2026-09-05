import api from './api'
import type {
  UsuarioResponse,
  LoginResponse,
  VerificarEmailPayload,
  ReenviarCodigoPayload,
} from '@/types/api'


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

export async function solicitarRecuperacaoSenha(email: string): Promise<string> {
  const { data } = await api.post<string>('/usuarios/esqueci-senha', { email })
  return data
}

export async function redefinirSenha(token: string, novaSenha: string): Promise<string> {
  const { data } = await api.post<string>('/usuarios/redefinir-senha', { token, novaSenha })
  return data
}

export async function excluirConta(senha: string): Promise<void> {
  await api.delete('/usuarios/me', { data: { senha } })
}

export async function verificarEmail(payload: VerificarEmailPayload): Promise<string> {
  const { data } = await api.post<string>('/usuarios/verificar-email', payload)
  return data
}

export async function reenviarCodigo(payload: ReenviarCodigoPayload): Promise<string> {
  const { data } = await api.post<string>('/usuarios/reenviar-codigo', payload)
  return data
}
