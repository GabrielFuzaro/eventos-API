# 🎟️ Eventos API

API REST desenvolvida em **Spring Boot** para gerenciamento de eventos e participantes, com controle automático de status, paginação, validações de negócio e tratamento global de exceções.

> Projeto de estudo/portfólio construído para aplicar na prática os conceitos de arquitetura em camadas, boas práticas REST e regras de negócio com Spring Boot.

---

## 📋 Funcionalidades

- **CRUD completo de eventos**: cadastro, listagem paginada, edição e exclusão
- **Cadastro de participantes** vinculados a um evento específico
- **Controle automático de status do evento**:
  - `ABERTO` — evento com vagas disponíveis
  - `LOTADO` — capacidade máxima atingida
  - `ENCERRADO` — evento encerrado automaticamente após a data (via job agendado)
- **Encerramento automático de eventos** via `@Scheduled`, sem necessidade de ação manual
- **Bloqueio de edição de eventos com data no passado**, prevenindo reabertura indevida de eventaos já encerrados
- **Filtro de eventos por status**, com paginação
- **Validação de e-mail duplicado**: impede que a mesma pessoa se inscreva duas vezes no mesmo evento
- **Tratamento global de exceções**, com respostas de erro padronizadas e mensagens de validação por campo

---

## 🛠️ Tecnologias

| Categoria         | Tecnologia                          |
|--------------------|--------------------------------------|
| Linguagem           | Java 8                              |
| Framework           | Spring Boot 2.7.18                  |
| Persistência        | Spring Data JPA / Hibernate         |
| Banco de dados      | MySQL                               |
| Migrations          | Flyway                              |
| Mapeamento de objetos | ModelMapper                       |
| Boilerplate         | Lombok                              |
| Validação           | Bean Validation (Jakarta Validation)|
| Build               | Maven                               |

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas, separando claramente as responsabilidades:

```
com.eventos.eventos_api
├── controller/          # Endpoints REST
├── domain/
│   ├── model/            # Entidades JPA e regras de negócio do domínio
│   ├── repository/       # Interfaces Spring Data JPA
│   ├── service/           # Regras de aplicação (CRUD, validações, jobs)
│   └── exception/         # Exceções de negócio customizadas
├── model/
│   ├── input/              # DTOs de entrada (request)
│   └── output/             # DTOs de saída (response)
├── assembler/             # Conversão entre Entity ↔ DTO
├── exceptionHandler/       # Tratamento global de exceções (@ControllerAdvice)
└── config/                 # Configurações gerais (CORS, etc.)
```

As entidades JPA nunca são expostas diretamente na API — toda entrada e saída passa por DTOs (`Input`/`Output`), montados através de *assemblers* dedicados.

---

## 🔌 Endpoints

### Eventos

| Método | Rota                              | Descrição                                  |
|--------|-------------------------------------|---------------------------------------------|
| GET    | `/eventos`                          | Lista todos os eventos (paginado)           |
| GET    | `/eventos/{id}`                     | Busca um evento específico                  |
| GET    | `/eventos/filtro/{statusEvento}`    | Lista eventos filtrados por status (paginado)|
| POST   | `/eventos`                          | Cadastra um novo evento                     |
| PUT    | `/eventos/{id}`                     | Atualiza um evento existente                |
| DELETE | `/eventos/{id}`                     | Remove um evento                            |

### Participantes

| Método | Rota                                | Descrição                                   |
|--------|--------------------------------------|-----------------------------------------------|
| GET    | `/participantes`                    | Lista todos os participantes (paginado)       |
| GET    | `/participantes/{id}`               | Busca um participante específico              |
| GET    | `/participantes/evento/{eventoId}`  | Lista participantes de um evento (paginado)   |
| POST   | `/participantes`                    | Cadastra um participante em um evento         |
| DELETE | `/participantes/{id}`               | Remove um participante                        |

---

## ▶️ Como executar

### Pré-requisitos

- Java 8 ou superior
- Maven
- MySQL em execução localmente

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/GabrielFuzaro/eventos-API.git
   cd eventos-API
   ```

2. Crie um banco de dados MySQL e configure as credenciais em `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/nome_do_banco
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```

3. Execute a aplicação (as migrations do Flyway rodam automaticamente na inicialização):
   ```bash
   ./mvnw spring-boot:run
   ```

4. A API estará disponível em `http://localhost:8080`

---

## 🚧 Próximos passos

- [ ] Documentação interativa com Swagger/OpenAPI
- [ ] Testes unitários e de integração para a camada de serviço
- [ ] Autenticação e autorização (Spring Security)

---

## 👤 Autor

**Gabriel Fuzaro**
[GitHub](https://github.com/GabrielFuzaro)

> Este é o back-end do sistema. O front-end (Angular) está disponível em [eventos-Front](https://github.com/GabrielFuzaro/eventos-Front).
