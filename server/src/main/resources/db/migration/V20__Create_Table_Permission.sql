-- [SQL-PERMISSION-001] Criação da tabela de permissões
CREATE TABLE IF NOT EXISTS permission (
    -- [SQL-PERMISSION-002] Chave primária (auto-incremento)
                                          id BIGSERIAL PRIMARY KEY,

    -- [SQL-PERMISSION-003] Descrição da permissão (ex: ROLE_ADMIN, ROLE_USER)
                                          description VARCHAR(255)
    );