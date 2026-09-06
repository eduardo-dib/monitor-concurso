export type FonteMonitoramento = 'MUNICIPAL' | 'ESTADUAL' | 'FEDERAL' | 'TODOS'
export type CategoriaContato = 'DUVIDA' | 'SUGESTAO' | 'RECLAMACAO' | 'ELOGIO' | 'OUTRO'
export type TipoAviso = 'INFO' | 'ALERTA' | 'MANUTENCAO'


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

export interface ExclusaoContaPayload {
  senha: string
}

export interface ContatoRequest {
  nome: string
  email: string
  categoria: CategoriaContato
  mensagem: string
}





export interface CriarAlertaPayload {
  palavrasChave: string
  estado: string
  municipio: string
  orgao: string
  fonte: FonteMonitoramento
}

export interface VerificarEmailPayload {
  email: string
  codigo: string
}

export interface ReenviarCodigoPayload {
  email: string
}

export interface AvisoResponse {
  id: number
  titulo: string
  mensagem: string
  tipo: TipoAviso
  ativo: boolean
  criadoEm: string
  expiraEm: string | null
}





//teste
