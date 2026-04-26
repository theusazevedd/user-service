# User Service

[![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?logo=gradle)](https://gradle.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-black)](https://jwt.io/)

Microservico responsavel por cadastro, autenticacao e gestao de dados de usuario (perfil, enderecos e telefones).

---

## Sumario

- [Visao Geral](#visao-geral)
- [Tecnologias](#tecnologias)
- [Como Executar Localmente](#como-executar-localmente)
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

### Pre-requisitos

- Java 17+
- PostgreSQL local
- Git
- Gradle (ou wrapper)

### 1) Clonar repositorio

```bash
git clone <url-do-repo>
cd user-service
```

### 2) Configurar variaveis de ambiente

Use o `.env.example` como base:

```bash
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

### 3) Criar banco

Crie o banco no PostgreSQL:

- `db_usuario`

### 4) Subir aplicacao

```bash
./gradlew bootRun
```

---

## Configuracao

`src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:postgresql://localhost:5432/db_usuario`
- `spring.datasource.username=${DB_USER}`
- `spring.datasource.password=${DB_PASSWORD}`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.jpa.show-sql=true`

### Observacoes para producao

- Trocar `ddl-auto=update` por estrategia controlada de migration (Flyway/Liquibase).
- Desabilitar `show-sql`.
- Externalizar e rotacionar segredo JWT.

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

```text
src/main/java/com/azevedo/user_service
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
