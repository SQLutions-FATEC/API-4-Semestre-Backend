-- =====================================================
-- Script: seed_usuarios_iniciais.sql
-- Objetivo: Criar usuários iniciais no banco de dados
-- Autor: Glória Brito
-- Data: 23/09/2025
-- Observação: Usuários não terão login funcional, apenas referência interna
-- =====================================================

-- ==========================
-- Inserir usuários padrão
-- ==========================
-- Campos da tabela usuario:
-- id     -> SERIAL PRIMARY KEY
-- nome   -> Nome do usuário
-- nivel  -> Perfil do usuário (ENUM: Admin, Gestor)
-- email  -> Email do usuário (único)
-- senha  -> Senha dummy para preencher o campo

INSERT INTO usuario (nome, nivel, email, senha) VALUES
  ('admin', 'Gestor', 'admin@sistema.com', '$2a$10$2.lQv/U.ouYG4T0Ek6naJey3GLsZZWit.Rz3pSN57SdTfIjV9XEYa');
-- Senha inserida: "admin"

-- ==========================
-- Conferência (opcional)
-- ==========================
-- Visualizar usuários inseridos
SELECT id, nome, nivel, email FROM usuario;

-- ==========================
-- Observações finais
-- ==========================
-- 1. Este script pode ser rodado em qualquer ambiente de banco de dados
--    com o modelo definido no arquivo .sql
-- 2. Como os usuários não terão login, o campo senha pode ser qualquer valor.
-- 4. Apenas Admin e Gestor poderão autenticar no sistema.
-- 5. Usuários como Cidadão são apenas referência e não precisam de senha funcional.
