-- [SQL-SUPPLIER-001] Criação da tabela de fornecedores
CREATE TABLE suppliers (
    -- [SQL-SUPPLIER-002] Chave primária (auto-incremento)
                           id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-SUPPLIER-003] INFORMAÇÕES BÁSICAS
    -- =====================================================

    -- [SQL-SUPPLIER-004] Nome do fornecedor (obrigatório)
                           name VARCHAR(150) NOT NULL,

    -- [SQL-SUPPLIER-005] Documento (CNPJ/CPF) - único e opcional
                           document VARCHAR(20) UNIQUE NOT NULL, -- CNPJ / CPF

    -- [SQL-SUPPLIER-006] Email para contato (opcional)
                           email VARCHAR(150),

    -- [SQL-SUPPLIER-007] Telefone para contato (opcional)
                           phone VARCHAR(20) NOT NULL,

    -- =====================================================
    -- [SQL-SUPPLIER-008] DADOS DE ENDEREÇO
    -- =====================================================

    -- [SQL-SUPPLIER-009] Código Postal (CEP)
                           zip_code VARCHAR(10) NOT NULL,

    -- [SQL-SUPPLIER-010] Logradouro (rua/avenida)
                           street VARCHAR(200) NOT NULL,

    -- [SQL-SUPPLIER-011] Número do endereço
                           street_number VARCHAR(20) NOT NULL,

    -- [SQL-SUPPLIER-012] Complemento (sala, andar, etc)
                           address_complement VARCHAR(100),

    -- [SQL-SUPPLIER-013] Bairro
                           neighborhood VARCHAR(100) NOT NULL,

    -- [SQL-SUPPLIER-014] Cidade
                           city VARCHAR(100) NOT NULL,

    -- [SQL-SUPPLIER-015] Estado (UF - 2 caracteres)
                           state CHAR(2) NOT NULL,

    -- =====================================================
    -- [SQL-SUPPLIER-016] STATUS E AUDITORIA
    -- =====================================================

    -- [SQL-SUPPLIER-017] Status do fornecedor (ativo/inativo)
                           active BOOLEAN DEFAULT TRUE,

    -- [SQL-SUPPLIER-018] Data de criação do registro
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- [SQL-SUPPLIER-019] Data da última atualização
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- =====================================================
    -- [SQL-SUPPLIER-020] FOTOS E CÓDIGOS
    -- =====================================================

    -- [SQL-SUPPLIER-021] URL da foto do fornecedor (com valor padrão - logo ERP)
                           photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/logoerp.png',

    -- [SQL-SUPPLIER-022] QR Code (com valor padrão - LinkedIn do dev)
                           qr_code VARCHAR(255) DEFAULT 'https://www.linkedin.com/in/oliveirawilliandev/',

    -- [SQL-SUPPLIER-023] Código de barras (com valor padrão de 13 zeros)
                           bar_code VARCHAR(255) DEFAULT '0000000000000'
);