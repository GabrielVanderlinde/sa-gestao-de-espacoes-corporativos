SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS espacos_corporativos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE espacos_corporativos;

DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS recursos;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    matricula VARCHAR(255) NOT NULL,
    data_nascimento DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recursos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    dias_semana_disponivel VARCHAR(500),
    data_inicial_agendamento DATE,
    data_final_agendamento DATE,
    hora_inicial_agendamento TIME,
    hora_final_agendamento TIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO usuarios (nome, email, senha, matricula, data_nascimento) VALUES
('Jonathan', 'jonathan@email.com', 'teste123', 'MAT001', '2000-05-10'),
('Maria Silva', 'maria@email.com', 'teste123', 'MAT002', '1995-08-22'),
('Carlos Souza', 'carlos@email.com', 'teste123', 'MAT003', '1988-12-01');

INSERT INTO recursos (descricao, tipo, dias_semana_disponivel, data_inicial_agendamento, data_final_agendamento, hora_inicial_agendamento, hora_final_agendamento) VALUES
('Sala de Reunião Alpha', 'sala', 'Segunda-feira, Terça-feira, Quarta-feira, Quinta-feira, Sexta-feira', '2026-01-01', '2026-12-31', '08:00', '18:00'),
('Sala de Reunião Beta', 'sala', 'Segunda-feira, Quarta-feira, Sexta-feira', '2026-01-01', '2026-12-31', '09:00', '17:00'),
('Projetor Portátil', 'equipamento', 'Segunda-feira, Terça-feira, Quarta-feira, Quinta-feira, Sexta-feira, Sábado', '2026-01-01', '2026-12-31', '08:00', '20:00');

INSERT INTO reservas (usuario_id, recurso_id, data, hora_inicial, hora_final, cancelamento, observacao) VALUES
(1, 1, '2026-07-20', '09:00', '11:00', NULL, 'Reunião de projeto'),
(2, 2, '2026-07-21', '10:00', '12:00', NULL, 'Apresentação'),
(3, 3, '2026-07-22', '14:00', '16:00', NULL, 'Treinamento');
