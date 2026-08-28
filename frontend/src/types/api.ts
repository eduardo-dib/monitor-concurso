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

export interface AlertaResponse {
  id: number
  palavrasChave: string
  estado: string
  municipio: string
  orgao: string
  ativo: boolean
  usuarioNome: string
  usuarioEmail: string
}





//teste
