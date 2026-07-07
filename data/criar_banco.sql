
-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS espacos_corporativos;
USE espacos_corporativos;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    matricula VARCHAR(255) NOT NULL,
    data_nascimento DATE
);

-- Tabela de Recursos (Espaços/Equipamentos)
CREATE TABLE IF NOT EXISTS recursos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    data_inicial_agendamento DATE,
    data_final_agendamento DATE,
    hora_inicial_agendamento TIME,
    hora_final_agendamento TIME
);

-- Tabela de Dias da Semana Disponíveis (para cada recurso)
CREATE TABLE IF NOT EXISTS recurso_dias_semana_disponivel (
    recurso_id BIGINT NOT NULL,
    dias_semana_disponivel VARCHAR(255),
    FOREIGN KEY (recurso_id) REFERENCES recursos(id)
);

-- Tabela de Reservas
CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    recurso_id BIGINT NOT NULL,
    data DATE NOT NULL,
    hora_inicial TIME NOT NULL,
    hora_final TIME NOT NULL,
    cancelamento DATE,
    observacao VARCHAR(255),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (recurso_id) REFERENCES recursos(id)
);

-- Usuário admin para teste (senha: admin123)
INSERT INTO usuarios (nome, email, senha, matricula, data_nascimento)
VALUES ('Administrador', 'admin@email.com', 'admin123', 'ADM001', '1990-01-15')
ON DUPLICATE KEY UPDATE nome = nome;

