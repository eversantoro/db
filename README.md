<<<<<<< HEAD
# API Banco Digital — db.tec.br

API REST simplificada para um banco digital: gestão de contas, transferência de valores entre contas, consulta de movimentações financeiras e notificações pós-transferência.

**Domínio:** [db.tec.br](https://db.tec.br)  
**Stack:** Java 21, Spring Boot 3.4, Maven, PostgreSQL 16, Docker

---

## Requisitos

| Item | Versão |
|------|--------|
| JDK | 21 (`C:\Program Files\Java\jdk-21`) |
| Maven | 3.9+ |
| Docker Desktop | Em execução (PostgreSQL) |

---

## Como rodar o projeto

### 1. Subir o PostgreSQL (Docker)

Na raiz do projeto:

```bash
docker compose up -d
```

Aguarde o healthcheck (`docker compose ps`).

### 2. Compilar e executar a API

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
mvn clean spring-boot:run
```

A API sobe em **http://localhost:8081** .

A raiz (`/`) redireciona automaticamente para o Swagger.

### 3. Documentação Swagger

- **Início (redirect):** http://localhost:8081/ → Swagger  
- **Swagger UI:** http://localhost:8081/swagger-ui/index.html  
- **Página alternativa:** http://localhost:8081/home  
- **OpenAPI JSON:** http://localhost:8081/api-docs  

### 4. Contas pré-carregadas

Na primeira execução com banco vazio, são criadas:

| ID | Nome | Saldo inicial |
|----|------|---------------|
| conta-001 | Ana Costa | R$ 5.000,00 |
| conta-002 | Bruno Lima | R$ 3.200,50 |
| conta-003 | Carla Mendes | R$ 150,00 |

---

## Endpoints principais

| Método | URL | Descrição |
|--------|-----|-----------|
| `POST` | `/api/v1/accounts` | Cadastrar conta (id, nome, saldo inicial) |
| `GET` | `/api/v1/accounts` | Listar contas |
| `GET` | `/api/v1/accounts/{id}` | Consultar conta |
| `GET` | `/api/v1/accounts/{id}/transactions` | Movimentações (extrato) |
| `POST` | `/api/v1/transfers` | Transferir entre contas |

### Exemplo: transferência

```http
POST /api/v1/transfers
Content-Type: application/json

{
  "fromAccountId": "conta-001",
  "toAccountId": "conta-002",
  "amount": 250.00
}
```

### Exemplo: nova conta

```http
POST /api/v1/accounts
Content-Type: application/json

{
  "id": "conta-004",
  "name": "Daniel Rocha",
  "initialBalance": 1000.00
}
```

---

## Testes unitários

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
mvn test
```

Os testes usam H2 em memória (`application-test.yml`), sem depender do Docker.

---

## Decisões de design e arquitetura

Esta seção descreve **por que** o projeto foi organizado assim, como isso se relaciona com **SOLID** e **Clean Architecture**, e o que seria diferente em um sistema maior.

### Visão geral do estilo adotado

O projeto segue uma **arquitetura em camadas** (layered architecture), padrão natural em Spring Boot para APIs REST de porte médio. Há **inspiração** em Clean Architecture — separação de responsabilidades, domínio com regras de negócio, casos de uso na camada de serviço, detalhes de infraestrutura nas bordas — mas **não** é Clean Architecture “pura” (sem módulos `usecase`/`adapter`, sem portas explícitas nem entidades livres de JPA).

Para o desafio técnico, essa escolha equilibra **clareza**, **tempo de entrega** e **comportamento correto** (transação, concorrência, extrato, notificação), sem a complexidade de um hexágono completo.

```
┌─────────────────────────────────────────────────────────────┐
│  api          Controllers, DTOs, validação HTTP, erros      │
├─────────────────────────────────────────────────────────────┤
│  service      Casos de uso: conta, transferência            │
├─────────────────────────────────────────────────────────────┤
│  domain       Entidades, regras (débito/crédito), repos     │
├─────────────────────────────────────────────────────────────┤
│  notification Eventos pós-commit (efeito colateral)         │
├─────────────────────────────────────────────────────────────┤
│  config       Spring, OpenAPI, CORS, seed, async            │
└─────────────────────────────────────────────────────────────┘
         PostgreSQL (Docker) · springdoc · logs
```

Pacote base: `br.tec.br` invertido → `br.tec.db`.

---

### Relação com Clean Architecture

| Princípio da Clean Architecture | Como aparece no projeto | Observação |
|--------------------------------|------------------------|------------|
| **Independência de frameworks** | Parcial | Domínio concentra regras (`Account.debit`/`credit`), mas entidades usam anotações JPA — acoplamento pragmático ao Hibernate. |
| **Regra de dependência** (camadas internas não dependem das externas) | Parcial | Fluxo principal: `api` → `service` → `domain`. Porém `service` importa DTOs de `api` (`TransferRequest`), o que inverte idealmente a seta (ver melhorias abaixo). |
| **Casos de uso** | Sim, de forma implícita | `AccountService` e `TransferService` orquestram fluxos; controllers não contêm regra de negócio. |
| **Entidades** | Sim | `Account` encapsula saldo e validação de débito/crédito. |
| **Adaptadores** | Implícitos | Controllers (entrada HTTP), repositórios Spring Data (saída BD), `NotificationService` (saída notificação). |
| **Frameworks/detalhes nas bordas** | Sim | SQL, OpenAPI, Docker e async ficam em `config`, JPA nos repositórios, HTTP em `api`. |

**Conclusão:** trata-se de **arquitetura em camadas alinhada a boas práticas Spring**, com **separação clara de papéis**, não de um hexágono formal. Em um produto bancário de larga escala, evoluiríamos para portas (`AccountRepository` como interface no domínio, implementação JPA em `infrastructure`) e comandos de aplicação sem depender de DTOs HTTP.

---

### Análise SOLID (como está implementado)

#### S — Single Responsibility (responsabilidade única)

Cada classe tende a um motivo claro para mudar:

- **`AccountController` / `TransferController`:** apenas traduzem HTTP ↔ serviços; não calculam saldo nem gravam extrato.
- **`AccountService`:** cadastro e consulta de contas e extrato.
- **`TransferService`:** orquestra uma transferência (lock, débito/crédito, lançamentos, evento).
- **`Account`:** invariantes de saldo (valor positivo, saldo suficiente no débito).
- **`NotificationService`:** único lugar que “envia” notificação (hoje via log).
- **`GlobalExceptionHandler`:** único lugar que mapeia exceções para HTTP.

`TransferService` concentra vários passos de **um** caso de uso (transferir). Isso é aceitável no escopo atual; se crescer, extrair validadores ou um `TransferExecutor` manteria o SRP sem espalhar regra no controller.

#### O — Open/Closed (aberto para extensão, fechado para modificação)

- **Notificações:** novos canais (e-mail, SMS) podem ser novos `@EventListener` ou implementações sem alterar `TransferService`, desde que escutem `TransferCompletedEvent`.
- **API:** novos endpoints = novos controllers ou métodos, sem mudar o núcleo de transferência.
- **Persistência:** trocar PostgreSQL por outro banco exigiria mais trabalho hoje (repositórios Spring Data acoplados ao JPA) — ponto de evolução para OCP pleno.

#### L — Liskov Substitution (substituição de Liskov)

Pouca herança no código; o princípio aparece sobretudo nos **testes**: mocks de `AccountRepository` e `ApplicationEventPublisher` substituem implementações reais sem quebrar `TransferService`. Contratos de interface Spring Data são estáveis para quem consome.

#### I — Interface Segregation (segregação de interface)

Repositórios são interfaces **enxutas** (`JpaRepository` + `findByIdForUpdate`), sem métodos genéricos obrigando o serviço a conhecer operações irrelevantes. Não há “interface gorda” de serviço exposta ao controller — cada serviço expõe só o necessário.

#### D — Dependency Inversion (inversão de dependência)

Serviços dependem de **abstrações** injetadas pelo Spring:

- `AccountRepository`, `FinancialTransactionRepository` (interfaces)
- `ApplicationEventPublisher` (abstração do framework para eventos)

Controllers dependem de `AccountService`/`TransferService`, não de JDBC nem de entidades JPA diretamente.

**Ressalva:** DTOs da camada `api` são usados dentro de `service`. O ideal em DIP estrito seria comandos/objetos da camada de aplicação e o controller apenas converter Request → Command → Response.

---

### Por que cada decisão foi tomada

#### 1. Camada `api` separada de `service`

**Motivo:** o contrato HTTP (JSON, status codes, Bean Validation, Swagger) muda por motivos diferentes das regras bancárias. Controllers finos delegam tudo ao serviço; assim, testes de negócio não precisam subir servlet nem montar JSON.

**Efeito:** qualquer cliente REST (Swagger, Postman, app mobile) consome a mesma superfície sem duplicar lógica.

#### 2. Regras de saldo dentro de `Account` (domínio)

**Motivo:** “saldo insuficiente” e “valor inválido” são regras do **negócio**, não do banco nem do JSON. Centralizar em `debit`/`credit` evita que um futuro batch ou mensageria replique validação incorreta no serviço.

**Efeito:** alinhado ao coração da Clean Architecture — entidade com comportamento, não apenas dados anêmicos.

#### 3. `TransferService` como orquestrador transacional

**Motivo:** transferência exige passos atômicos: travar contas, debitar, creditar, persistir duas movimentações, só então notificar. Um único `@Transactional` garante **consistência**; se qualquer passo falhar, nada parcial fica gravado.

**Efeito:** simula cenário real de concorrência; o serviço é o “caso de uso” da transferência.

#### 4. Lock pessimista + ordem de IDs + `@Version`

**Motivo:** o desafio cita alta concorrência. `PESSIMISTIC_WRITE` evita duas threads debitando o mesmo saldo ao mesmo tempo. Ordenar IDs antes do lock (**lock ordering**) reduz **deadlock** quando duas transferências cruzadas ocorrem em paralelo. `@Version` adiciona camada extra de detecção de conflito otimista no Hibernate.

**Efeito:** resiliência e performance previsível sem lógica espalhada no controller.

#### 5. Extrato em `financial_transactions` com `transferId` compartilhado

**Motivo:** requisito de **consulta de movimentações** com rastreabilidade. Cada transferência gera par débito/crédito ligado pelo mesmo UUID — auditoria e suporte a extrato por conta.

**Efeito:** modelo de dados expressa o negócio; consulta via `GET .../transactions` não reconstrói histórico só pelo saldo atual.

#### 6. Notificação por evento após commit (`AFTER_COMMIT` + `@Async`)

**Motivo:** notificar **só se** a transferência foi persistida com sucesso; falha no “envio” de e-mail não deve desfazer dinheiro. Processamento assíncrono não bloqueia a resposta HTTP — melhor **performance** percebida.

**Efeito:** desacoplamento entre núcleo financeiro e canal de comunicação (hoje log; amanhã SMTP/API).

#### 7. Exceções de negócio + `GlobalExceptionHandler`

**Motivo:** regras lançam exceções semânticas (`BusinessException`, `ResourceNotFoundException`); um único handler traduz para 404/409/422 com corpo padronizado (`ErrorResponse`).

**Efeito:** API previsível para integradores; controllers sem `try/catch` repetido (SRP no tratamento de erro).

#### 8. PostgreSQL via Docker e JPA `ddl-auto: update`

**Motivo:** ambiente reproduzível para quem avalia o desafio; JPA acelera entrega sem scripts manuais no primeiro run.

**Efeito:** foco no comportamento da API; em produção usaria Flyway/Liquibase e perfis (`dev`/`prod`).

#### 9. Testes unitários com H2 e Mockito

**Motivo:** CI e desenvolvedor rodam testes sem Docker; serviços testados com dependências mockadas validam regras e eventos isoladamente.

**Efeito:** feedback rápido; reforça DIP nos testes.

---

### O que evoluiria em um sistema maior (gap honesto)

| Hoje | Evolução natural |
|------|------------------|
| DTO HTTP usado no `service` | Commands/queries na camada `application` |
| JPA nas entidades de `domain` | Entidade de domínio + mapper + entidade JPA em `infrastructure` |
| Repositório Spring Data no pacote `domain` | Interface no domínio, implementação em `infrastructure` |
| Notificação por log | Adapter `EmailNotificationAdapter` implementando porta `NotificationPort` |
| Um monólito Maven | Módulos `domain`, `application`, `api`, `infrastructure` |

Esses passos aproximariam a implementação de **Clean Architecture e SOLID completos**, sem invalidar o que já está correto para o escopo do desafio.

---

### Consistência, concorrência e dados (resumo técnico)

- Transferências em **transação única** (`@Transactional`).
- **Lock pessimista** ao carregar contas para transferência.
- **Ordenação de IDs** antes do lock para reduzir deadlock.
- **`@Version`** para concorrência otimista complementar.
- **PostgreSQL** via Docker Compose; credenciais em `application.yml` (desenvolvimento local).
- **Swagger/OpenAPI** (springdoc) para documentação e testes manuais.

---

## Estrutura do repositório

```
db/
├── docker-compose.yml
├── pom.xml
├── README.md
└── src/
    ├── main/java/br/tec/db/
    └── test/java/br/tec/db/
```

---

## Entregável: repositório público

Publicado no git

---

## Licença

Projeto desenvolvido para o desafio técnico Java — DB Tecnologia (db.tec.br).
=======
# db
>>>>>>> f0fbe7d6f4b4961903a91afd6b05c28920c351b4
