# User Service

[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?logo=gradle)](https://gradle.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-black)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://docs.docker.com/compose/)

Microservico responsavel por cadastro, autenticacao e gestao de dados de usuario (perfil, enderecos e telefones).

---

## Sumario

- [Visao Geral](#visao-geral)
- [Tecnologias](#tecnologias)
- [Como Executar Localmente](#como-executar-localmente)
- [Execucao com Docker](#execucao-com-docker)
- [Configuracao](#configuracao)
- [Autenticacao e Seguranca](#autenticacao-e-seguranca)
- [API](#api)
- [Contratos (DTOs)](#contratos-dtos)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)

---

## Visao Geral

O `user-service` e a fonte de verdade para identidade e dados cadastrais do usuario.

### Responsabilidades

- Criar usuario
- Autenticar usuario (`/usuario/login`)
- Buscar e atualizar dados de usuario
- Cadastrar e atualizar endereco
- Cadastrar e atualizar telefone

---

## Tecnologias

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Gradle

---

## Como Executar Localmente

O codigo-fonte da aplicacao Spring Boot fica no subdiretorio `user-service/` (projeto Gradle). A raiz do repositorio contem orquestracao Docker (`Dockerfile`, `docker-compose.yml`).

### Pre-requisitos

- Java 17+
- PostgreSQL acessivel em `localhost:5432`
- Git
- Gradle Wrapper (incluido no modulo; nao e necessario Gradle instalado globalmente)

### 1) Clonar o repositorio

```bash
git clone <url-do-repo>
cd <pasta-do-repositorio>
```

### 2) Configurar variaveis de ambiente

Para execucao **sem Docker**, a aplicacao le `DB_USER` e `DB_PASSWORD` (veja [Configuracao](#configuracao)).

Copie o exemplo e ajuste os valores:

```bash
cp user-service/.env.example user-service/.env
```

Edite `user-service/.env` com credenciais validas do seu PostgreSQL local. Alternativamente, exporte as variaveis no shell antes de subir a aplicacao.

### 3) Criar o banco de dados

Crie o banco `db_usuario` (ou o nome alinhado a `spring.datasource.url` em `application.properties`) no PostgreSQL.

### 4) Subir a aplicacao

Na pasta do **modulo Gradle**:

```bash
cd user-service
./gradlew bootRun
```

No Windows (PowerShell ou CMD), pode usar `gradlew.bat` em vez de `./gradlew`.

---

## Execucao com Docker

A stack containerizada sobe a API e o PostgreSQL com um unico comando, sem depender de JAR pre-compilado na maquina host.

### Arquitetura

| Componente | Descricao |
|------------|-----------|
| `Dockerfile` | Build multi-stage: estagio `build` compila com Gradle dentro da imagem; estagio `runtime` usa apenas JRE Alpine e executa o JAR como usuario nao-root. |
| `docker-compose.yml` | Servicos `app` (API) e `db` (PostgreSQL), com `healthcheck` no banco e `depends_on` com `service_healthy` para evitar falha de conexao na subida. |
| `.dockerignore` | Reduz o contexto de build (exclui artefatos locais, `.git`, caches Gradle, etc.). |

### Pre-requisitos

- [Docker Engine](https://docs.docker.com/engine/install/) e [Docker Compose V2](https://docs.docker.com/compose/) (por exemplo, Docker Desktop no Windows)

### Variaveis de ambiente (Compose)

Na **raiz do repositorio**, o Compose le automaticamente um arquivo `.env` (se existir) para interpolar variaveis. O arquivo `.env` nao deve ser versionado; use `.env.example` como modelo.

| Variavel | Uso | Valor padrao no compose (se omitida no `.env`) |
|----------|-----|-----------------------------------------------|
| `POSTGRES_DB` | Nome do banco criado no container Postgres | `db_usuario` |
| `POSTGRES_USER` | Usuario do banco | `postgres` |
| `POSTGRES_PASSWORD` | Senha do banco | `postgres` |

As mesmas variaveis alimentam o servico `db` e, via interpolacao, as propriedades `SPRING_DATASOURCE_*` do servico `app`, garantindo consistencia entre aplicacao e banco.

### Subir a stack

Na raiz do repositorio:

```bash
docker compose up --build
```

Em ambientes que ainda expoem o binario legado, o comando equivalente e `docker-compose up --build`.

- API: `http://localhost:8080`
- PostgreSQL exposto em `localhost:5432` (util para ferramentas locais; em producao, avalie nao publicar a porta do banco)

### Encerrar

```bash
docker compose down
```

Para remover tambem volumes nomeados (dados do Postgres), use `docker compose down -v` quando fizer sentido no seu ambiente.

---

## Configuracao

### Execucao local (Gradle)

Arquivo: `user-service/src/main/resources/application.properties`

- `spring.datasource.url=jdbc:postgresql://localhost:5432/db_usuario`
- `spring.datasource.username=${DB_USER}`
- `spring.datasource.password=${DB_PASSWORD}`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.show-sql=true`

Variaveis esperadas: `DB_USER`, `DB_PASSWORD` (definidas via `user-service/.env` na sua IDE, export no shell, ou outro mecanismo de env do SO).

### Execucao com Docker Compose

O Compose injeta `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` no container da aplicacao, sobrescrevendo a URL `localhost` do `application.properties` e apontando para o servico `db` na rede interna do Compose.

### Observacoes para producao

- Trocar `ddl-auto=update` por estrategia controlada de migration (Flyway/Liquibase).
- Desabilitar `show-sql`.
- Externalizar e rotacionar segredo JWT.
- Em ambientes containerizados, fixar tags de imagem (por exemplo, `postgres:<versao>` em vez de `latest`) e gerenciar segredos via mecanismo apropriado ao orquestrador (Secrets, Vault, etc.), evitando credenciais em arquivos versionados.

---

## Autenticacao e Seguranca

- Autenticacao via Bearer Token (JWT).
- Sessao stateless.
- Endpoints publicos:
  - `POST /usuario`
  - `POST /usuario/login`
- Demais endpoints em `/usuario/**` exigem token.

> O response de usuario nao expoe senha.

---

## API

Base path: `/usuario`

### Auth

#### `POST /usuario/login`

Autentica usuario e retorna token JWT.

Request:

```json
{
  "email": "user@email.com",
  "senha": "123456"
}
```

Response:

```text
Bearer <jwt_token>
```

### Usuario

#### `POST /usuario`

Cria usuario.

#### `GET /usuario?email={email}`

Busca usuario por email (autenticado).

#### `PUT /usuario`

Atualiza dados do usuario autenticado (via token).

#### `DELETE /usuario/{email}`

Remove usuario por email.

### Endereco

#### `POST /usuario/endereco`

Cadastra endereco para o usuario autenticado.

#### `PUT /usuario/endereco?id={id}`

Atualiza endereco por id.

### Telefone

#### `POST /usuario/telefone`

Cadastra telefone para o usuario autenticado.

#### `PUT /usuario/telefone?id={id}`

Atualiza telefone por id.

---

## Contratos (DTOs)

### UsuarioRequestDTO

```json
{
  "nome": "Matheus",
  "email": "matheus@email.com",
  "senha": "123456",
  "enderecos": [
    {
      "rua": "Rua A",
      "numero": 100,
      "complemento": "Apto 10",
      "cidade": "Sao Paulo",
      "estado": "SP",
      "cep": "01001000"
    }
  ],
  "telefones": [
    {
      "numero": "999999999",
      "ddd": "011"
    }
  ]
}
```

### UsuarioResponseDTO

```json
{
  "nome": "Matheus",
  "email": "matheus@email.com",
  "enderecos": [
    {
      "id": 1,
      "rua": "Rua A",
      "numero": 100,
      "complemento": "Apto 10",
      "cidade": "Sao Paulo",
      "estado": "SP",
      "cep": "01001000"
    }
  ],
  "telefones": [
    {
      "id": 1,
      "numero": "999999999",
      "ddd": "011"
    }
  ]
}
```

> `senha` so existe em request e nao e retornada.

---

## Estrutura do Projeto

Visao resumida do repositorio:

```text
.
├── Dockerfile                 # build multi-stage da API
├── docker-compose.yml         # app + PostgreSQL
├── .dockerignore              # contexto de build reduzido para o Docker
├── .env.example               # modelo para variaveis do Compose (raiz)
└── user-service/              # modulo Gradle (Spring Boot)
    ├── .env.example           # modelo para DB_USER / DB_PASSWORD (execucao local)
    ├── build.gradle
    ├── gradlew
    └── src/main/java/com/azevedo/user_service
        ├── business
        │   ├── converter
        │   ├── dto
        │   └── service
        ├── controller
        └── infrastructure
            ├── entity
            ├── exceptions
            ├── repository
            └── security
```

---

## Testes

```bash
./gradlew test
```
