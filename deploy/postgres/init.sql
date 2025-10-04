\c api;
-- Criar tipo ENUM para veículos
CREATE TYPE tipo_veiculo AS ENUM (
    'Carro',
    'Camionete',
    'Ônibus',
    'Van',
    'Caminhão grande',
    'Moto',
    'Indefinido'
);

CREATE TYPE nivel_usuario AS ENUM (
    'Admin',
    'Gestor'
);

-- Endereço
CREATE TABLE endereco (
    id SERIAL PRIMARY KEY,
    ende VARCHAR(150) NOT NULL UNIQUE,
    regiao VARCHAR(6) 
);

-- Radar
CREATE TABLE radar (
    id VARCHAR(9) PRIMARY KEY, -- camera_numero
    id_end INT NOT NULL, --refere-se ao id do endereço
    position GEOMETRY(Point, 4326),
    vel_reg INT NOT NULL,
    CONSTRAINT fk_radar_endereco FOREIGN KEY (id_end) REFERENCES endereco(id)
);

-- Leitura
CREATE TABLE leitura (
    id SERIAL PRIMARY KEY,
    id_rad VARCHAR(9) NOT NULL, --refere-se ao id do radar
    dat_hora TIMESTAMP NOT NULL,
    tip_vei tipo_veiculo NOT NULL,
    vel INT NOT NULL,
    CONSTRAINT fk_leitura_radar FOREIGN KEY (id_rad) REFERENCES radar(id)
);

-- Usuário
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    nivel nivel_usuario NOT NULL
);
