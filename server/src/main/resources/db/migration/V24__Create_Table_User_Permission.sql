-- [SQL-USER-PERM-001] Criação da tabela de associação usuário-permissão (se não existir)
CREATE TABLE IF NOT EXISTS user_permission (
    -- [SQL-USER-PERM-002] ID do usuário (obrigatório - FK para users)
                                               id_user BIGINT NOT NULL,

    -- [SQL-USER-PERM-003] ID da permissão (obrigatório - FK para permission)
                                               id_permission BIGINT NOT NULL,

    -- [SQL-USER-PERM-004] Chave primária composta (garante unicidade do par)
                                               PRIMARY KEY (id_user, id_permission),

    -- [SQL-USER-PERM-005] Relacionamento com tabela users
    CONSTRAINT fk_user_permission FOREIGN KEY (id_user) REFERENCES users(id),

    -- [SQL-USER-PERM-006] Relacionamento com tabela permission
    CONSTRAINT fk_user_permission_permission FOREIGN KEY (id_permission) REFERENCES permission(id)
    );