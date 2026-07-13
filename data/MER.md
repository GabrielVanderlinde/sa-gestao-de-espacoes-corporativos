# Modelo de Entidade e Relacionamento (MER)

## Entidades

### 1. USUARIOS
| Campo | Tipo | Restrição |
|-------|------|-----------|
| id | BIGINT | PK, Auto-incremento |
| nome | VARCHAR(255) | NOT NULL |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| senha | VARCHAR(255) | NOT NULL |
| matricula | VARCHAR(255) | NOT NULL |
| data_nascimento | DATE | |

### 2. RECURSOS
| Campo | Tipo | Restrição |
|-------|------|-----------|
| id | BIGINT | PK, Auto-incremento |
| descricao | VARCHAR(255) | NOT NULL |
| tipo | VARCHAR(255) | NOT NULL |
| dias_semana_disponivel | VARCHAR(500) | |
| data_inicial_agendamento | DATE | |
| data_final_agendamento | DATE | |
| hora_inicial_agendamento | TIME | |
| hora_final_agendamento | TIME | |

### 3. RESERVAS
| Campo | Tipo | Restrição |
|-------|------|-----------|
| id | BIGINT | PK, Auto-incremento |
| usuario_id | BIGINT | FK → USUARIOS.id, NOT NULL |
| recurso_id | BIGINT | FK → RECURSOS.id, NOT NULL |
| data | DATE | NOT NULL |
| hora_inicial | TIME | NOT NULL |
| hora_final | TIME | NOT NULL |
| cancelamento | DATE | |
| observacao | VARCHAR(255) | |

## Relacionamentos

### USUARIOS ←→ RESERVAS
- **Tipo**: 1:N (Um para Muitos)
- **Cardinalidade**: Um usuário pode ter N reservas
- **Cardinalidade**: Uma reserva pertence a apenas 1 usuário

### RECURSOS ←→ RESERVAS
- **Tipo**: 1:N (Um para Muitos)
- **Cardinalidade**: Um recurso pode ter N reservas
- **Cardinalidade**: Uma reserva referência apenas 1 recurso

## Diagrama

```
┌──────────────────────────┐
│        USUARIOS          │
├──────────────────────────┤
│ id (PK)                  │
│ nome                     │
│ email (UNIQUE)           │
│ senha                    │
│ matricula                │
│ data_nascimento          │
└──────────┬───────────────┘
           │ 1
           │
           │ N
┌──────────┴───────────────┐
│         RESERVAS         │
├──────────────────────────┤
│ id (PK)                  │
│ usuario_id (FK)          │
│ recurso_id (FK)          │
│ data                     │
│ hora_inicial             │
│ hora_final               │
│ cancelamento             │
│ observacao               │
└──────────┬───────────────┘
           │ N
           │
           │ 1
┌──────────┴───────────────┐
│        RECURSOS          │
├──────────────────────────┤
│ id (PK)                  │
│ descricao                │
│ tipo                     │
│ dias_semana_disponivel   │
│ data_inicial_agendamento │
│ data_final_agendamento   │
│ hora_inicial_agendamento │
│ hora_final_agendamento   │
└──────────────────────────┘
```

## Regras de Negócio

1. Um usuário pode criar múltiplas reservas
2. Um recurso pode receber múltiplas reservas
3. Uma reserva deve referenciar um usuário e um recurso existentes
4. A data da reserva deve estar dentro do período disponível do recurso
5. O dia da semana deve estar nos dias disponíveis do recurso
6. O horário deve estar dentro do horário permitido do recurso
7. Não pode haver conflito de agendamento (mesmo recurso, data e horário)
8. Máximo de 5 reservas ativas por usuário
