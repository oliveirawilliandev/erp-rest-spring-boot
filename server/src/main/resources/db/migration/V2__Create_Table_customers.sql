-- [SQL-CUSTOMER-001] Criação da tabela de clientes
CREATE TABLE customers (
    -- [SQL-CUSTOMER-002] Chave primária (auto-incremento)
                           id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-CUSTOMER-003] INFORMAÇÕES BÁSICAS
    -- =====================================================

    -- [SQL-CUSTOMER-004] Nome do cliente (obrigatório)
                           name VARCHAR(150) NOT NULL,

    -- [SQL-CUSTOMER-005] Email - único e opcional
                           email VARCHAR(150),

    -- [SQL-CUSTOMER-006] Telefone para contato
                           phone VARCHAR(20) NOT NULL,

    -- [SQL-CUSTOMER-007] Documento (CPF/CNPJ) - único e opcional
                           document VARCHAR(20) UNIQUE,

    -- =====================================================
    -- [SQL-CUSTOMER-008] DADOS DE ENDEREÇO
    -- =====================================================

    -- [SQL-CUSTOMER-009] Código Postal (CEP)
                           zip_code VARCHAR(10) NOT NULL,

    -- [SQL-CUSTOMER-010] Logradouro (rua/avenida)
                           street VARCHAR(200) NOT NULL,

    -- [SQL-CUSTOMER-011] Número do endereço
                           street_number VARCHAR(20) NOT NULL,

    -- [SQL-CUSTOMER-012] Complemento (apartamento, bloco, etc)
                           address_complement VARCHAR(100),

    -- [SQL-CUSTOMER-013] Bairro
                           neighborhood VARCHAR(100) NOT NULL,

    -- [SQL-CUSTOMER-014] Cidade
                           city VARCHAR(100) NOT NULL,

    -- [SQL-CUSTOMER-015] Estado (UF - 2 caracteres)
                           state CHAR(2) NOT NULL,

    -- =====================================================
    -- [SQL-CUSTOMER-016] STATUS E AUDITORIA
    -- =====================================================

    -- [SQL-CUSTOMER-017] Status do cliente (ativo/inativo)
                           active BOOLEAN DEFAULT TRUE,

    -- [SQL-CUSTOMER-018] Data de criação (preenchida automaticamente)
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- [SQL-CUSTOMER-019] Data de última atualização
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- =====================================================
    -- [SQL-CUSTOMER-020] FOTOS E CÓDIGOS
    -- =====================================================

    -- [SQL-CUSTOMER-021] URL da foto do cliente (com valor padrão - logo ERP)
                           photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/00_some_person.jpg',

    -- [SQL-CUSTOMER-022] QR Code (com valor padrão - LinkedIn do dev)
                           qr_code VARCHAR(255) DEFAULT 'https://www.linkedin.com/in/oliveirawilliandev/',

    -- [SQL-CUSTOMER-023] Código de barras (com valor padrão de 13 zeros)
                           bar_code VARCHAR(255) DEFAULT '0000000000000'
);