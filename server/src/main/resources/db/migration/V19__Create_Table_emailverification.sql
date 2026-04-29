-- [SQL-EMAIL-VERIF-001] Criação da tabela de verificação de email
CREATE TABLE email_verification (
    -- [SQL-EMAIL-VERIF-002] Chave primária (auto-incremento)
                                    id BIGSERIAL PRIMARY KEY,

    -- [SQL-EMAIL-VERIF-003] Email do destinatário (obrigatório)
                                    email VARCHAR(255) NOT NULL,

    -- [SQL-EMAIL-VERIF-004] Código de verificação (ex: 6 dígitos alfanuméricos)
                                    code VARCHAR(20) NOT NULL,

    -- [SQL-EMAIL-VERIF-005] Data/hora de criação do código
                                    created_at TIMESTAMP NOT NULL,

    -- [SQL-EMAIL-VERIF-006] Data/hora de expiração do código (ex: +15 minutos)
                                    expires_at TIMESTAMP NOT NULL,

    -- [SQL-EMAIL-VERIF-007] Indica se o código já foi utilizado (padrão: FALSE)
                                    used BOOLEAN DEFAULT FALSE
);