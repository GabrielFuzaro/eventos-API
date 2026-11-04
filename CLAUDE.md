# CLAUDE.md — Eventos API

API REST em Spring Boot para gerenciar eventos, participantes, categorias e lista de espera.
O front-end Angular que consome esta API fica no repositório `eventos-Front`.

## Stack e comandos

- **Java 8** (`<java.version>8</java.version>`): não use `var`, `List.of`, `Optional.isEmpty`, records, text blocks
  nem nada além do Java 8.
- Spring Boot 2.7.18, Spring Data JPA/Hibernate, Spring Security + JWT (jjwt), Flyway, MySQL,
  Lombok, ModelMapper, Apache POI (Excel), Bean Validation (`javax.validation`, não `jakarta`).
- Pacotes de persistência e transação: `javax.persistence.*` e `javax.transaction.Transactional`
  (é o padrão do projeto, mantenha).

```bash
./mvnw -DskipTests compile      # compilar
./mvnw spring-boot:run          # rodar (porta 8080)
./mvnw test                     # testes
```

Para rodar é preciso:
- MySQL em `localhost:3306` (banco `Eventos`, criado automaticamente; usuário `root`, sem senha).
- Variável de ambiente `JWT_SECRET` com pelo menos 32 caracteres (HS256).

## Arquitetura (camadas)

```
com.eventos.eventos_api
├── controller/            # Endpoints REST. Só recebem input, chamam service e devolvem output
├── domain/
│   ├── model/             # Entidades JPA + regras do próprio domínio (ex.: Evento.encerrar(), Evento.jaOcorreu())
│   ├── repository/        # Interfaces Spring Data JPA
│   ├── service/           # Regras de negócio e transações
│   └── exception/         # NegocioException (400) e EntidadeNaoEncontradaException (404)
├── model/input/           # DTOs de entrada (*Input)
├── model/output/          # DTOs de saída (*Output)
├── assembler/             # Conversão Entity -> Output (*Assembler)
├── exceptionHandler/      # ApiExceptionHandler (@ControllerAdvice) + Problema
└── config/                # CORS, ModelMapper, segurança (JWT, filtros, usuário admin inicial)
```

### Regras de projeto

- **Nunca exponha entidades** nos controllers: entrada por `*Input`, saída por `*Output` montado num `*Assembler`.
- **Regra de negócio fica no service** (ou na entidade, se for do próprio domínio), nunca no controller.
- Métodos de service que alteram dados levam `@Transactional`.
- Injeção de dependência por construtor com `@AllArgsConstructor` (padrão do projeto).
- **Cuidado com dependência circular entre services.** `ParticipanteService` depende de `CrudEventoService`
  e de `ListaEsperaService`, e `CrudEventoService` depende de `ListaEsperaService`. Por isso `ListaEsperaService`
  usa os repositories diretamente e **não pode** depender de `CrudEventoService` nem de `ParticipanteService`.
- Erros de regra de negócio: `throw new NegocioException("mensagem")` → HTTP 400.
  Recurso inexistente: `EntidadeNaoEncontradaException` → HTTP 404.
  O corpo do erro é um `Problema` (`status`, `dataHora`, `titulo`, `campos`). O front exibe `titulo`,
  então **a mensagem deve ser em português e legível para o usuário final**.
- Listagens usam `Pageable` / `Page<>` (o front envia `page`, `size` e `sort`).
- Nomes de classes, métodos, variáveis, rotas e mensagens em **português**, seguindo o padrão existente
  (`cadastrar`, `listar...`, `buscar...`, `excluir...`). Alguns campos usam snake_case por herança
  (`data_evento`, `capacidade_maxima`) porque o front já depende deles. Não renomeie.
- Comentários curtos com `//` explicando o "porquê" são o padrão do projeto.

### Banco de dados (Flyway)

- Migrations em `src/main/resources/db/migration`, nomeadas `V0NN__descricao-com-hifens.sql`.
- **Nunca edite uma migration que já existe.** Toda mudança de schema é uma nova migration com o próximo número.
- O schema é controlado só pelo Flyway: não use `ddl-auto` para criar tabelas.
- Ao excluir uma entidade com filhos (FK), apague os filhos antes (ex.: a lista de espera antes do evento).

### Segurança

| Rota                         | Acesso                    |
|------------------------------|---------------------------|
| `/auth/**`                   | público (login/cadastro)  |
| `GET /eventos/**`            | público                   |
| `POST /eventos/**`           | autenticado               |
| `PUT` / `DELETE /eventos/**` | `ADMIN`                   |
| `POST /participantes/**`     | autenticado               |
| `DELETE /participantes/**`   | `ADMIN`                   |
| `POST /lista-espera/**`      | autenticado               |
| `DELETE /lista-espera/**`    | `ADMIN`                   |
| qualquer outra rota          | autenticado               |

Toda rota nova deve ser declarada em `SecurityConfig` com o nível de acesso correto.
O CORS libera apenas `http://localhost:4200`.

## Regras de negócio

### Eventos
- Campos obrigatórios: nome (até 30 caracteres), local (até 30), capacidade máxima, data do evento e categoria.
- **Não é permitido cadastrar nem editar um evento com data no passado.**
- Status (`StatusEvento`):
  - `ABERTO`: tem vagas.
  - `LOTADO`: participantes ≥ capacidade máxima.
  - `ENCERRADO`: a data já passou. O `EncerramentoAutomaticoService` roda a cada 60s e encerra eventos
    `ABERTO`/`LOTADO` com `data_evento` anterior a agora.
- O status `ABERTO`/`LOTADO` é **sempre recalculado** pela quantidade de participantes. Nunca é escolhido pelo usuário.
- Um evento "já ocorreu" (`Evento.jaOcorreu()`) se está `ENCERRADO` **ou** se a data já passou,
  mesmo que o job ainda não tenha rodado.
- **Só é possível excluir eventos sem participantes inscritos.**
- Ao editar um evento, o status é recalculado. Se a capacidade aumentar, as novas vagas são preenchidas
  pela lista de espera.

### Participantes
- Nome (até 30) e email (até 40) são obrigatórios.
- O mesmo email não pode se inscrever duas vezes no mesmo evento, nem estar ao mesmo tempo como
  participante e na lista de espera desse evento.
- Não é possível se inscrever em evento `LOTADO` (a pessoa deve usar a lista de espera)
  nem em evento que já ocorreu.
- Somente `ADMIN` exclui participantes.
- A exportação para Excel (`GET /participantes/excel?eventoId=`) inclui apenas participantes,
  não a lista de espera.

### Lista de espera
- Só se entra na lista de espera se o evento **está lotado e ainda não ocorreu**.
- A fila é por ordem de chegada (id crescente; o `id` é auto incremento). A posição 1 é a próxima a ser chamada.
- **Quando um participante sai, o primeiro da fila vira participante automaticamente** e sai da fila
  (`ListaEsperaService.preencherVagas`). Isso só acontece se o evento ainda não ocorreu.
- `preencherVagas` também é chamado ao editar o evento (aumento de capacidade) e sempre recalcula o status.
- Somente `ADMIN` remove pessoas da fila.
- Ao excluir o evento, a fila dele é apagada antes.
- Nenhuma notificação é enviada a quem é promovido.

### Categorias
- Categorias base vêm da migration V007 (Palestra, Workshop, Show, Curso, Networking, Esporte).
- Todo evento precisa de uma categoria existente.

### Usuários e autenticação
- Papéis: `ADMIN` e `USER`. `POST /auth/cadastro` sempre cria `USER`.
- Na inicialização, o `DataInitializer` cria o usuário `admin` (senha `123456`) se não existir
  (apenas para desenvolvimento).
- `POST /auth/login` devolve um JWT (texto puro) com `sub` = username e claim `role`, válido por 1 hora.
- Senhas sempre com `BCryptPasswordEncoder`. Nunca devolva a senha em nenhum output.

## Regras de commit

- Mensagens em **português**, no formato Conventional Commits: `tipo: descrição curta no imperativo/presente`.
  - `feat:` nova funcionalidade
  - `fix:` correção de bug
  - `refactor:` mudança de código sem mudar comportamento
  - `docs:` documentação (README, CLAUDE.md, comentários)
  - `chore:` build, dependências, configuração
  - `test:` testes
  - `style:` formatação, sem mudança de lógica
- Primeira linha com até ~72 caracteres, em minúsculas e sem ponto final.
  Ex.: `feat: lista de espera para eventos lotados`.
- Se precisar, deixe uma linha em branco e detalhe em tópicos (`- ...`) o que mudou e por quê.
- Um commit por assunto. Não misture funcionalidade com refatoração não relacionada.
- O código precisa compilar (`./mvnw -DskipTests compile`) antes do commit.
- Nunca faça commit de segredos (`JWT_SECRET`, senhas reais), de `target/` ou de arquivos da IDE.
- Mudanças que alteram contrato da API (rotas, campos de input/output) devem ser acompanhadas
  da mudança correspondente no `eventos-Front`, de preferência com o mesmo nome de branch nos dois repositórios.
- Trabalhe em branch própria e abra Pull Request para a `main`. Não faça push direto na `main`.
