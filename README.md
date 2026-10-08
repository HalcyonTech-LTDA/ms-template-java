# 🚀 Golden Path Project Template (Java & Spring Boot)

Bem-vindo ao **Golden Path Project Template** (`ms-template-java`). Este repositório serve como a base técnica oficial e o modelo arquitetural para o desenvolvimento de novos microsserviços na organização. Ele engloba as melhores práticas de Clean Architecture, build moderno com Gradle (Kotlin DSL, Composite Builds), segurança por padrão e resiliência.

## 📋 Pré-requisitos

Para rodar e desenvolver neste projeto, as seguintes ferramentas são necessárias no seu ambiente:

- **[Java 26](https://adoptium.net/)** (via SDKMAN! recomendado)
- **[Docker](https://docs.docker.com/get-docker/)** e **Docker Compose**
- **[Task](https://taskfile.dev/)** (Task runner moderno, substituto do `Make`)
- **[Lefthook](https://github.com/evilmartians/lefthook)** (Para hooks do Git, instalado globalmente via NPM/Homebrew)
- *Opcionais, porém recomendados:* `trivy`, `semgrep`, `gitleaks`, `k6`, `newman`. (O `Taskfile` faz fallback automático para Docker caso não os encontre nativamente).

## 🏗️ Arquitetura e Estrutura do Projeto

Este template segue a **Clean Architecture** (Arquitetura Hexagonal / Ports and Adapters) e **Screaming Architecture**, agrupando os pacotes por capacidades de negócio e não por detalhes técnicos.

```text
app/src/main/java/com/example/templatejava/
├── common/                  # Infraestrutura compartilhada (ex: Exception Handlers, configs de agendamento)
├── customer/                # Capacidade de Negócio: Cliente
│   ├── domain/              # Lógica core, 100% puro (sem anotações Spring/Mongo)
│   ├── application/         # Casos de uso (Use Cases) / Interactors
│   └── infrastructure/      # Adaptadores (REST, Mongo, Feign, Jobs)
└── order/                   # Capacidade de Negócio: Pedido
```

### Regras de Ouro
1. **Domínio Puro**: O pacote `domain` não pode conhecer classes de infraestrutura, web ou persistência.
2. **Value Objects**: Evite *Primitive Obsession* — utilize *records* para modelar conceitos imutáveis.
3. **In-Memory Fakes**: Prefira usar Fakes (ex: `InMemoryCustomerRepository`) nos testes de `application` para isolamento e velocidade superior aos frameworks de Mock.

## 🚀 Como Executar Localmente

Utilizamos o **Task** para padronizar todos os comandos comuns no ciclo de vida do projeto. Para ver a lista completa de comandos, basta rodar:

```bash
task
```

### Perfis de Execução (`api` vs `scheduling`)

A aplicação é projetada para rodar em contextos isolados para escalabilidade horizontal otimizada:
- **`api`**: Sobe o servidor web e expõe os endpoints HTTP e REST. (Profile Spring: `api`)
- **`scheduling`**: Executa jobs assíncronos e processos em background (ex: sincronização de clientes). (Profile Spring: `scheduling`)

### Inicializando a Aplicação

Suba a infraestrutura base (MongoDB e dependências core) e em seguida a aplicação.

```bash
# Sobe banco de dados e dependências core
task infra:core

# Roda o módulo web (API)
task run:api

# Roda os workers (Scheduling)
task run:scheduling
```

Ou, via Docker Compose:
```bash
# Levanta os containers da API e Scheduling
task docker:app:up
```

## 🛠️ Comandos do Taskfile

Aqui estão os principais comandos disponíveis no `Taskfile.yaml`:

### Build & Testes
- `task check`: Roda toda a suíte de verificação (unitários, integração com Testcontainers, coverage Jacoco, check de formatação Spotless e ArchUnit).
- `task build`: Compila a aplicação gerando o JAR.
- `task test:unit`: Executa testes unitários rápidos.
- `task test:integration`: Executa testes de integração (sobe banco localmente via Testcontainers).
- `task test:mutation`: Executa testes de mutação com Pitest para garantir a resiliência das validações de domínio.

### Segurança (SCA e SAST)
- `task security:gitleaks`: Verifica credenciais e secrets expostos no código.
- `task security:trivy`: Escaneia o código e a imagem Docker em busca de vulnerabilidades (CVEs) em dependências.
- `task security:sast`: Varredura de segurança com Semgrep contra o OWASP Top 10.
- `task security:sbom`: Gera o Software Bill of Materials (CycloneDX).

### Testes de Carga e API
- `task newman`: Executa testes de API automatizados com Newman/Postman.
- `task k6:smoke` / `k6:load` / `k6:stress`: Bateria de testes de performance usando k6.

### Migrações de Banco de Dados (migrate-mongo)
- `task migrate:status`: Exibe o status das migrações (aplicadas vs pendentes).
- `task migrate:up`: Aplica todas as migrações pendentes no MongoDB.
- `task migrate:down`: Reverte a última migração aplicada.
- `task migrate:create -- <nome>`: Cria um novo script de migração versionado em `migrate-mongo/migrations/`.
- Mais detalhes e opções de execução via Docker em [`migrate-mongo/README.md`](migrate-mongo/README.md).

## 🤝 Submissão de PRs e Contribuição

Garantimos a qualidade desde o commit utilizando **Lefthook** e **Commitlint**.

1. **Configure o repositório na primeira vez**:
   ```bash
   task setup:hooks
   ```
2. Crie uma nova branch a partir de `main`.
3. Certifique-se de que o código segue o *Spotless Format* e os testes de arquitetura passam rodando `task check`.
4. Os commits devem seguir a convenção de **Conventional Commits** (ex: `feat: add order creation endpoint`, `fix: validate email formatting`).
5. As regras do Lefthook farão o lint da mensagem do commit, analisarão possíveis vazamentos de secrets e checarão formatação antes que você possa criar o commit com sucesso.

---

*Nota: Um mecanismo oficial de Scaffolding para renomear este repositório base para seu novo microsserviço estará disponível em breve. Siga as instruções deste README para desenvolvimento local.*
