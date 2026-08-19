export interface CadastroResponse {
  id: number
  nome: string
  email: string
}

export interface ApiErrorResponse {
  status: number
  mensagem: string
  endpoint: string
}
