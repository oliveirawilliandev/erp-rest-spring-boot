-- [SQL-USER-001] Criação da tabela de usuários (se não existir)
CREATE TABLE IF NOT EXISTS users (
    -- [SQL-USER-002] Chave primária (auto-incremento)
                                     id BIGSERIAL PRIMARY KEY,

    -- [SQL-USER-003] Nome de usuário para login (único)
                                     user_name VARCHAR(255) UNIQUE,

    -- [SQL-USER-004] Nome completo do usuário
    full_name VARCHAR(255),

    -- [SQL-USER-005] Senha do usuário (armazenada com hash)
    password VARCHAR(255),

    -- [SQL-006] Email - único e opcional
    email VARCHAR(150) UNIQUE,

    -- [SQL-USER-007] Indica se a conta não expirou
    account_non_expired BOOLEAN,

    -- [SQL-USER-008] Indica se a conta não está bloqueada
    account_non_locked BOOLEAN,

    -- [SQL-USER-009] Indica se as credenciais não expiraram
    credentials_non_expired BOOLEAN,

    -- [SQL-USER-010] Indica se o usuário está habilitado
    enabled BOOLEAN,

    -- [SQL-011] URL da foto do funcionário (com valor padrão)
    photo_url VARCHAR(500) DEFAULT 'https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/00_some_person.jpg'

    );