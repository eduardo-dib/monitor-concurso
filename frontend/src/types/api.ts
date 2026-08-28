export interface UsuarioResponse {
  id: number
  nome: string
  email: string
}

export interface LoginResponse {
  nome: string
}

export interface ApiErrorResponse {
  status: number
  mensagem: string
  endpoint: string
}
//teste
