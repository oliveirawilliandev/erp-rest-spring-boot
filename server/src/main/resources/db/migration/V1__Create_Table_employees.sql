-- [SQL-001] Criação da tabela de funcionários
CREATE TABLE employees (
    -- [SQL-002] Chave primária (auto-incremento)
                           id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-003] DADOS PESSOAIS
    -- =====================================================

    -- [SQL-004] Primeiro nome do funcionário (obrigatório)
                           first_name VARCHAR(150) NOT NULL,

    -- [SQL-005] Sobrenome do funcionário (opcional)
                           last_name VARCHAR(150),

    -- [SQL-006] CPF - único e opcional (pode ser nulo)
                           cpf VARCHAR(14) UNIQUE,

    -- [SQL-007] Gênero (obrigatório) - valores: MALE, FEMALE, OTHER
                           gender VARCHAR(20) NOT NULL,

    -- [SQL-008] Data de nascimento (obrigatória)
                           birth_date DATE NOT NULL,

    -- =====================================================
    -- [SQL-009] DADOS DE CONTATO
    -- =====================================================

    -- [SQL-010] Email - único e opcional
                           email VARCHAR(150) UNIQUE,

    -- [SQL-011] Telefone fixo (opcional)
                           phone VARCHAR(20),

    -- [SQL-012] Telefone celular (opcional)
                           mobile_phone VARCHAR(20),

    -- =====================================================
    -- [SQL-013] DADOS DE ENDEREÇO
    -- =====================================================

    -- [SQL-014] CEP (opcional)
                           zip_code VARCHAR(10),

    -- [SQL-015] Logradouro (opcional)
                           street VARCHAR(200),

    -- [SQL-016] Número do endereço (opcional)
                           street_number VARCHAR(20),

    -- [SQL-017] Complemento do endereço (opcional)
                           address_complement VARCHAR(100),

    -- [SQL-018] Bairro (opcional)
                           neighborhood VARCHAR(100),

    -- [SQL-019] Cidade (opcional)
                           city VARCHAR(100),

    -- [SQL-020] Estado - UF (2 caracteres, opcional)
                           state CHAR(2),

    -- =====================================================
    -- [SQL-021] DADOS PROFISSIONAIS
    -- =====================================================

    -- [SQL-022] Cargo do funcionário (obrigatório)
                           job_title VARCHAR(100) NOT NULL,

    -- [SQL-023] Departamento (opcional)
                           department VARCHAR(100),

    -- [SQL-024] Data de contratação (obrigatória)
                           hire_date DATE NOT NULL,

    -- [SQL-025] Data de desligamento (opcional)
                           termination_date DATE,

    -- [SQL-026] Status ativo/inativo (padrão: TRUE)
                           active BOOLEAN DEFAULT TRUE,

    -- =====================================================
    -- [SQL-027] DADOS DE AUDITORIA
    -- =====================================================

    -- [SQL-028] Data de criação (preenchida automaticamente)
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- [SQL-029] Data de última atualização (preenchida automaticamente)
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- =====================================================
    -- [SQL-030] FOTOS E CÓDIGOS
    -- =====================================================

    -- [SQL-031] URL da foto do funcionário (com valor padrão)
                           photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/00_some_person.jpg',

    -- [SQL-032] QR Code (com valor padrão - LinkedIn do dev)
                           qr_code VARCHAR(255) DEFAULT 'https://www.linkedin.com/in/oliveirawilliandev/',

    -- [SQL-033] Código de barras (com valor padrão de 13 zeros)
                           bar_code VARCHAR(255) DEFAULT '0000000000000'
);