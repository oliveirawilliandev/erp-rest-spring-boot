-- [SQL-PRODUCT-001] Criação da tabela de produtos
CREATE TABLE products (
    -- [SQL-PRODUCT-002] Chave primária (auto-incremento)
                          id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-PRODUCT-003] INFORMAÇÕES BÁSICAS DO PRODUTO
    -- =====================================================

    -- [SQL-PRODUCT-004] Nome do produto (obrigatório)
                          name VARCHAR(150) NOT NULL,

    -- [SQL-PRODUCT-005] Descrição detalhada do produto (texto longo)
                          description TEXT,

    -- [SQL-PRODUCT-006] Preço atual do produto (obrigatório) - 2 casas decimais
                          price NUMERIC(10,2) NOT NULL,

    -- [SQL-PRODUCT-007] Preço inicial/de custo (obrigatório) - 2 casas decimais
                          starting_price NUMERIC(10,2) NOT NULL,

    -- [SQL-PRODUCT-008] Quantidade em estoque (obrigatório)
                          stock_quantity INT NOT NULL,

    -- =====================================================
    -- [SQL-PRODUCT-009] STATUS E AUDITORIA
    -- =====================================================

    -- [SQL-PRODUCT-010] Status do produto (ativo/inativo) - padrão ativo
                          active BOOLEAN DEFAULT TRUE,

    -- [SQL-PRODUCT-011] Data de criação (preenchida automaticamente)
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- [SQL-PRODUCT-012] Data de última atualização
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- =====================================================
    -- [SQL-PRODUCT-013] FOTOS E CÓDIGOS
    -- =====================================================

    -- [SQL-PRODUCT-014] URL da foto do produto (com valor padrão - logo ERP)
                          photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/logoerp.png',

    -- [SQL-PRODUCT-015] QR Code (com valor padrão - LinkedIn do dev)
                          qr_code VARCHAR(255) DEFAULT 'https://www.linkedin.com/in/oliveirawilliandev/',

    -- [SQL-PRODUCT-016] Código de barras (com valor padrão de 13 zeros)
                          bar_code VARCHAR(255) DEFAULT '0000000000000'
);