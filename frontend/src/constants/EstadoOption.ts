export interface EstadoOption {
  sigla: string
  nome: string
  coberto: boolean
}

export const ESTADOS: EstadoOption[] = [
  { sigla: 'AC', nome: 'Acre', coberto: false },
  { sigla: 'AL', nome: 'Alagoas', coberto: true },
  { sigla: 'AP', nome: 'Amapá', coberto: false },
  { sigla: 'AM', nome: 'Amazonas', coberto: false },
  { sigla: 'BA', nome: 'Bahia', coberto: true },
  { sigla: 'CE', nome: 'Ceará', coberto: true },
  { sigla: 'DF', nome: 'Distrito Federal', coberto: true },
  { sigla: 'ES', nome: 'Espírito Santo', coberto: true },
  { sigla: 'GO', nome: 'Goiás', coberto: true },
  { sigla: 'MA', nome: 'Maranhão', coberto: true },
  { sigla: 'MT', nome: 'Mato Grosso', coberto: true },
  { sigla: 'MS', nome: 'Mato Grosso do Sul', coberto: true },
  { sigla: 'MG', nome: 'Minas Gerais', coberto: true },
  { sigla: 'PA', nome: 'Pará', coberto: false },
  { sigla: 'PB', nome: 'Paraíba', coberto: false },
  { sigla: 'PR', nome: 'Paraná', coberto: true },
  { sigla: 'PE', nome: 'Pernambuco', coberto: false },
  { sigla: 'PI', nome: 'Piauí', coberto: true },
  { sigla: 'RJ', nome: 'Rio de Janeiro', coberto: false },
  { sigla: 'RN', nome: 'Rio Grande do Norte', coberto: false },
  { sigla: 'RS', nome: 'Rio Grande do Sul', coberto: false },
  { sigla: 'RO', nome: 'Rondônia', coberto: false },
  { sigla: 'RR', nome: 'Roraima', coberto: false },
  { sigla: 'SC', nome: 'Santa Catarina', coberto: false },
  { sigla: 'SP', nome: 'São Paulo', coberto: false },
  { sigla: 'SE', nome: 'Sergipe', coberto: false },
  { sigla: 'TO', nome: 'Tocantins', coberto: false },
]
