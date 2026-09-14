export interface EstadoOption {
  sigla: string
  nome: string
  coberto: boolean
}

export const ESTADOS: EstadoOption[] = [
  { sigla: 'AC', nome: 'Acre', coberto: true },
  { sigla: 'AL', nome: 'Alagoas', coberto: true },
  { sigla: 'AP', nome: 'Amapá', coberto: true },
  { sigla: 'AM', nome: 'Amazonas', coberto: true },
  { sigla: 'BA', nome: 'Bahia', coberto: true },
  { sigla: 'CE', nome: 'Ceará', coberto: true },
  { sigla: 'DF', nome: 'Distrito Federal', coberto: true },
  { sigla: 'ES', nome: 'Espírito Santo', coberto: true },
  { sigla: 'GO', nome: 'Goiás', coberto: true },
  { sigla: 'MA', nome: 'Maranhão', coberto: true },
  { sigla: 'MT', nome: 'Mato Grosso', coberto: true },
  { sigla: 'MS', nome: 'Mato Grosso do Sul', coberto: true },
  { sigla: 'MG', nome: 'Minas Gerais', coberto: true },
  { sigla: 'PA', nome: 'Pará', coberto: true },
  { sigla: 'PB', nome: 'Paraíba', coberto: false },
  { sigla: 'PR', nome: 'Paraná', coberto: true },
  { sigla: 'PE', nome: 'Pernambuco', coberto: true },
  { sigla: 'PI', nome: 'Piauí', coberto: true },
  { sigla: 'RJ', nome: 'Rio de Janeiro', coberto: true },
  { sigla: 'RN', nome: 'Rio Grande do Norte', coberto: true },
  { sigla: 'RS', nome: 'Rio Grande do Sul', coberto: false },
  { sigla: 'RO', nome: 'Rondônia', coberto: false },
  { sigla: 'RR', nome: 'Roraima', coberto: false },
  { sigla: 'SC', nome: 'Santa Catarina', coberto: true },
  { sigla: 'SP', nome: 'São Paulo', coberto: true },
  { sigla: 'SE', nome: 'Sergipe', coberto: false },
  { sigla: 'TO', nome: 'Tocantins', coberto: true },
]
