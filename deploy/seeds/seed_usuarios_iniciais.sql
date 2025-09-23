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
  ('Administrador', 'Admin', 'admin@sistema.com', 'senha_dummy'),  -- usuário com login
  ('Gestor', 'Gestor', 'gestor@sistema.com', 'senha_dummy'),      -- usuário com login
  ('Cidadão', 'Gestor', 'cidadao@sistema.com', 'senha_dummy');    -- usuário sem login


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
