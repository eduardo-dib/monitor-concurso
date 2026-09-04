# VigiaConcursos
## https://vigiaconcursohomolog.duckdns.org/

Sistema de monitoramento de concursos públicos que varre diários oficiais brasileiros (municipais, estaduais e, futuramente, federal) em busca de palavras-chave cadastradas pelo usuário, enviando alertas por e-mail sempre que encontra uma publicação nova.

> Projeto pessoal, construído do zero, com foco em decisões de arquitetura e segurança de nível produtivo.

---

## O problema que resolve

Concursos públicos são divulgados em dezenas de diários oficiais diferentes, cada um com seu próprio site, formato e regras de busca. Acompanhar manualmente é inviável. O VigiaConcursos automatiza essa varredura e centraliza os resultados em um único fluxo de alertas por e-mail.

## Cobertura atual

| Âmbito | Cobertura |
|---|---|
| **Municipal** | Todos os municípios brasileiros, via integração com a API pública do [Querido Diário](https://queridodiario.ok.org.br/) (projeto da Open Knowledge Brasil) |
| **Estadual** | 15 estados com integração dedicada (ver tabela abaixo) |

### Estados com integração dedicada

| UF | Tipo de acesso | Observação técnica |
|----|----|----|
| PR | Scraping HTML | Token CSRF por sessão + captcha ocasional |
| GO | API JSON oficial | |
| MT | API JSON oficial | Mesma plataforma de GO |
| MS | API JSON não-oficial | Link direto pro PDF completo |
| DF | API JSON (POST form-data) | Formato de link não-óbvio |
| AL | API JSON oficial (POST JSON) | |
| BA | Elasticsearch | Certificado SSL não-padrão |
| CE | API JSON (WordPress AJAX) | Nonce dinâmico por sessão |
| MA | API híbrida (JSON + HTML embutido) | Scroll do Elasticsearch expira rápido |
| PI | API JSON | Sem filtro de data no servidor |
| ES | API JSON | Mesma plataforma de GO/MT, com resolução de ID em duas etapas |
| MG | API JSON oficial | Autenticação via JWT efêmero de sessão anônima |
| SP | API JSON oficial | Formato de data sem zero à esquerda |
| AM | API JSON oficial | |
| PA | Scraping HTML (ASP.NET) | |
| PE | API JSON oficial | |

Cada integração foi construída a partir de engenharia reversa manual (captura de tráfego real via DevTools), já que nenhuma delas expõe documentação pública de API.

## Funcionalidades

- Cadastro de alertas com palavras-chave, filtro por estado/município e fonte (municipal, estadual ou ambos)
- Verificação de e-mail obrigatória no cadastro (código de 6 dígitos)
- Autenticação via cookie `httpOnly` (sem exposição de token a XSS)
- Recuperação de senha com token hasheado (SHA-256) e invalidação automática de tokens anteriores
- Exclusão de conta em conformidade com a LGPD
- E-mails de alerta agregados por execução (evita ser marcado como spam)
- Rate limiting nos endpoints sensíveis (login, recuperação de senha, verificação de e-mail, formulário de contato)
- Observabilidade via Grafana + Prometheus (latência, taxa de erro, uso de memória/CPU)

## Arquitetura

O núcleo do sistema é um agendador (`MonitoramentoScheduler`) que injeta dinamicamente, via CDI, todas as implementações de duas interfaces:

- `DiarioOficialScraper` — para fontes que exigem parsing de HTML
- `DiarioOficialClient` — para fontes com API JSON

Ambas retornam o mesmo formato de saída (`PublicacaoScraped`). Isso significa que adicionar um novo estado é só criar uma nova classe `@ApplicationScoped` implementando uma das duas interfaces — nada precisa ser registrado manualmente em lugar nenhum do scheduler.

```
┌─────────────────────┐
│ MonitoramentoScheduler│
└──────────┬───────────┘
           │ injeta via CDI
           ▼
┌──────────────────────┬──────────────────────┐
│ DiarioOficialScraper  │  DiarioOficialClient  │
│ (parsing de HTML)     │  (consumo de API JSON)│
└──────────────────────┴──────────────────────┘
           │
           ▼
   PublicacaoScraped (formato comum)
           │
           ▼
   MonitoramentoService → e-mail agregado
```

## Stack

**Backend**
- Java 21 + Quarkus 3.x
- Hibernate ORM / Panache
- SmallRye JWT
- Micrometer + Prometheus
- Jsoup (scraping) / MicroProfile REST Client (APIs)
- PostgreSQL

**Frontend**
- Vue 3 + TypeScript
- Pinia
- Tailwind CSS v4
- Axios

**Infraestrutura**
- Oracle Cloud (Docker + Docker Compose + Caddy)
- Grafana + Prometheus
- Brevo (SMTP)

## Rodando localmente

Pré-requisitos: Java 21, Node 18+, Docker.

```bash
# Backend
git clone https://github.com/eduardo-dib/monitor-concurso.git
cd monitor-concurso/backend
cp .env.example .env   # preencher variáveis de banco e SMTP
docker compose up -d   # sobe o PostgreSQL
./mvnw quarkus:dev      # http://localhost:8080

# Frontend
cd ../frontend
npm install
npm run dev             # http://localhost:5173
```

A documentação interativa da API fica disponível em `/q/swagger-ui` quando o backend está rodando.

## Em desenvolvimento

- Cobertura estadual: faltam alguns estados sem API pública conhecida
- Proteção definitiva dos endpoints de teste usados durante o desenvolvimento das integrações estaduais
- Área de perfil do usuário

<img width="1883" height="936" alt="image" src="https://github.com/user-attachments/assets/abde51ae-c229-4363-8d43-a6d16e275672" />
<img width="1894" height="944" alt="image" src="https://github.com/user-attachments/assets/e3e89ceb-1779-4ec4-94c4-b39a31e3c9c4" />
<img width="1901" height="941" alt="image" src="https://github.com/user-attachments/assets/9ca41b7e-c3dc-4bc1-8fc3-3ada9037182a" />



## Autor

**Eduardo Cardozo Dib**
Desenvolvedor de Sistemas — [github.com/eduardo-dib](https://github.com/eduardo-dib)
