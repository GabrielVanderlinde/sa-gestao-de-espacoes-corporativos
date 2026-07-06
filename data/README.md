# Configuração do Banco de Dados

## Pré-requisitos
- MySQL 8.0 ou superior instalado e rodando
- Usuário root do MySQL sem senha (ou ajuste no `application.properties`)

## Como criar o banco

### Opção 1: Usando o MySQL Workbench
1. Abra o MySQL Workbench
2. Conecte-se ao MySQL local
3. Abra o arquivo `criar_banco.sql`
4. Execute o script (botão de play verde)

### Opção 2: Usando o terminal
```bash
mysql -u root -p < criar_banco.sql
```

### Opção 3: Criar manualmente
1. Abra o MySQL (Workbench ou linha de comando)
2. Execute: `CREATE DATABASE espacos_corporativos;`
3. O Hibernate cria as tabelas automaticamente na primeira execução

## Verificação
Após criar o banco, inicie a aplicação:
```bash
./mvnw spring-boot:run
```

Acesse: http://localhost:8080/login

## Usuário de teste
- E-mail: admin@email.com
- Senha: admin123
