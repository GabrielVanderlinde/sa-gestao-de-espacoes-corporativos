# Gestão de Espaços Corporativos

Sistema web para gerenciamento de reservas de espaços e recursos corporativos (salas, equipamentos).

## Pré-requisitos

- Java 21+
- MySQL 8.0+ (ou Docker)
- Maven 3.8+

## Como Rodar

### Opção 1: Docker (Recomendado)

```bash
docker compose up --build
```

O Docker cria automaticamente:
- Banco de dados `espacos_corporativos` com 3 usuários e 3 recursos pré-cadastrados
- Tabelas criadas pelo Hibernate
- Encoding UTF-8 para caracteres especiais (ã, ç, etc.)

Acesse: http://localhost:8080/login

Para parar:
```bash
docker compose down
```

Para limpar dados:
```bash
docker compose down -v
```

### Opção 2: Manual

1. Criar banco no MySQL:
```sql
CREATE DATABASE espacos_corporativos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Executar script de dados iniciais:
```bash
mysql -u root -p espacos_corporativos < data/criar_banco.sql
```

3. Rodar a aplicação:
```bash
./mvnw spring-boot:run
```

## Credenciais de Teste

| Usuário | E-mail | Senha |
|---------|--------|-------|
| Jonathan | jonathan@email.com | teste123 |
| Maria Silva | maria@email.com | teste123 |
| Carlos Souza | carlos@email.com | teste123 |

## Rotas HTTP

### Autenticação
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/login` | Tela de login |
| POST | `/login` | Realizar login |
| GET | `/logout` | Encerrar sessão |

### Usuários
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/usuariolista` | Listar usuários |
| GET | `/usuarioinserir` | Formulário de cadastro |
| POST | `/usuarioinserir` | Cadastrar usuário |
| GET | `/usuarioatualizar/{id}` | Formulário de edição |
| POST | `/usuarioatualizar` | Atualizar usuário |
| DELETE | `/usuarioexcluir/{id}` | Excluir usuário |

### Espaços/Recursos
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/recursolista` | Listar recursos |
| GET | `/recursoinserir` | Formulário de cadastro |
| POST | `/recursoinserir` | Cadastrar recurso |
| GET | `/recursoatualizar/{id}` | Formulário de edição |
| POST | `/recursoatualizar` | Atualizar recurso |
| DELETE | `/recursoexcluir/{id}` | Excluir recurso |

### Reservas
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/reservalista` | Listar reservas |
| GET | `/reservainserir` | Formulário de cadastro |
| POST | `/reservainserir` | Cadastrar reserva |
| GET | `/reservaatualizar/{id}` | Formulário de edição |
| POST | `/reservaatualizar` | Atualizar reserva |
| GET | `/reservacancelar/{id}` | Tela de cancelamento |
| POST | `/reservacancelar` | Cancelar reserva |
| DELETE | `/reservaexcluir/{id}` | Excluir reserva |

### APIs REST (Inovações)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/recurso/{id}/status` | Status do recurso (Livre/Ocupado) |
| GET | `/api/recurso/{id}/horarios` | Horários disponíveis do recurso |
| GET | `/api/usuario/{id}/reservas` | Contagem de reservas do usuário |

## Validações Implementadas

### Usuário (RF01)
- Nome obrigatório
- E-mail obrigatório, formato válido e único no sistema
- Senha obrigatória na criação, mínimo 5 caracteres, com letras e números
- Matrícula obrigatória
- Data de nascimento: não pode ser futura, máximo 500 anos
- Atualização: email único entre usuários, senha opcional

### Espaço/Recurso (RF02)
- Descrição obrigatória
- Tipo obrigatório
- Data inicial: hoje ou futura
- Data final: hoje ou futura e deve ser ≥ data inicial
- Hora inicial deve ser anterior à hora final
- Dias da semana: seleção via checkboxes

### Reserva (RF04)
- Usuário obrigatório (seleção via dropdown)
- Recurso obrigatório (seleção via dropdown)
- Data obrigatória e dentro do período disponível do recurso
- Dia da semana deve estar nos dias disponíveis do recurso
- Horário deve estar dentro do horário permitido do recurso
- Máximo 5 reservas ativas por usuário
- Conflito de agendamento: mesmo recurso, data e horário sobreposto
- Cancelamento: mínimo 1 dia de antecedência
- Tela de cancelamento: dados somente leitura, motivo obrigatório

### Autenticação (RF05)
- Controle de acesso por sessão
- Redirecionamento automático para login se não autenticado
- Logout encerra a sessão

## Estrutura do Projeto

```
src/main/java/com/senai/gestao_de_espacos_corporativos/
├── controllers/       # Controllers HTTP
│   ├── PageController.java
│   ├── LoginController.java
│   ├── UsuarioController.java
│   ├── RecursoController.java
│   ├── ReservaController.java
│   ├── LoginInterceptor.java
│   └── WebConfig.java
├── services/          # Lógica de negócio
│   ├── LoginService.java
│   ├── UsuarioService.java
│   ├── RecursoService.java
│   └── ReservaService.java
├── repositories/      # Acesso ao banco (JPA)
│   ├── UsuarioRepository.java
│   ├── RecursoRepository.java
│   └── ReservaRepository.java
├── entities/          # Entidades JPA
│   ├── UsuarioEntity.java
│   ├── RecursoEntity.java
│   └── ReservaEntity.java
└── dtos/              # Objetos de transferência
    ├── LoginDto.java
    ├── UsuarioDto.java
    ├── RecursoDto.java
    └── ReservaDto.java

src/main/resources/
├── templates/         # Páginas HTML (Thymeleaf)
├── static/            # Arquivos estáticos (CSS, JS, imagens)
│   ├── css/style.css
│   ├── images/
│   ├── excluir_usuario.js
│   ├── excluir_recurso.js
│   └── excluir_reserva.js
└── application.properties

data/
└── criar_banco.sql    # Script de criação do banco com dados iniciais

postman/
└── Gestao-Espacos-Corporativos.postman_collection.json  # Coleção Postman

der/
└── DER.png            # Diagrama de Entidade e Relacionamento (RF06)

mer/
└── MER.md             # Modelo de Entidade e Relacionamento detalhado
```

## Postman

A pasta `postman/` contém a coleção completa de requisições para testar a API:

- **Autenticação**: Login, Logout
- **Usuários**: CRUD completo
- **Espaços/Recursos**: CRUD completo
- **Reservas**: CRUD + Cancelamento
- **APIs REST**: Status, Horários, Contagem

Para importar:
1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `Gestao-Espacos-Corporativos.postman_collection.json`

## Funcionalidades

### Requisitos Obrigatórios
- **RF01**: Cadastro de usuários com validações completas
- **RF02**: Cadastro de espaços/equipamentos com checkboxes
- **RF04**: Cadastro de reservas com validações e conflitos
- **RF05**: Controle de acesso por sessão
- **RF06**: Diagrama de Entidade e Relacionamento (DER)

### Inovações (RF07)
- **Indicador visual**: Badge verde (Livre) / vermelho (Ocupado) nos recursos
- **Horário automático**: Preenche horários ao selecionar recurso
- **Limite de reservas**: Máximo 5 reservas ativas por usuário
- **Validações avançadas**: Conflito de agendamento, período e dia da semana

## Tecnologias

- Java 21
- Spring Boot 4.0.7
- Spring Data JPA
- Thymeleaf
- MySQL 8.0
- Bootstrap 5.3
- Docker
- Poppins (fonte)

## Equipe

- Projeto SA 3ª Fase - Técnico em Desenvolvimento de Sistemas
- SENAI Blumenau/SC
