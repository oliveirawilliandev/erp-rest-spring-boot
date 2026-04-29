-- [SQL-INGREDIENT-001] Criação da tabela de insumos (matéria-prima para padaria)
CREATE TABLE ingredients (
    -- [SQL-INGREDIENT-002] Chave primária (auto-incremento)
                             id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-INGREDIENT-003] INFORMAÇÕES BÁSICAS DO INSUMO
    -- =====================================================

    -- [SQL-INGREDIENT-004] Nome do insumo (obrigatório) - Ex: Farinha de Trigo, Açúcar, Fermento
                             name VARCHAR(150) NOT NULL,

    -- [SQL-INGREDIENT-005] Descrição detalhada do insumo (texto longo)
                             description TEXT,

    -- [SQL-INGREDIENT-006] Preço de compra do insumo (obrigatório) - 2 casas decimais
                             purchase_price NUMERIC(10,2) NOT NULL,

    -- =====================================================
    -- [SQL-INGREDIENT-007] CONTROLE DE ESTOQUE
    -- =====================================================

    -- [SQL-INGREDIENT-008] Quantidade atual em estoque (obrigatório)
                             stock_quantity INT NOT NULL DEFAULT 0,

    -- [SQL-INGREDIENT-009] Estoque mínimo para alerta (obrigatório)
                             minimum_stock INT NOT NULL DEFAULT 0,

    -- [SQL-INGREDIENT-010] Unidade de medida (obrigatório) - kg, g, L, mL, un, pacote, etc.
                             unit_of_measure VARCHAR(20) NOT NULL,

    -- =====================================================
    -- [SQL-INGREDIENT-011] STATUS E AUDITORIA
    -- =====================================================

    -- [SQL-INGREDIENT-012] Status do insumo (ativo/inativo) - padrão ativo
                             active BOOLEAN DEFAULT TRUE,

    -- [SQL-INGREDIENT-013] Data de criação (preenchida automaticamente)
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- [SQL-INGREDIENT-014] Data de última atualização
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- =====================================================
    -- [SQL-INGREDIENT-015] FOTOS E CÓDIGOS
    -- =====================================================

    -- [SQL-INGREDIENT-016] URL da foto do insumo (com valor padrão - logo ERP)
                             photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/logoerp.png',

    -- [SQL-INGREDIENT-017] QR Code (com valor padrão - LinkedIn do dev)
                             qr_code VARCHAR(255) DEFAULT 'https://www.linkedin.com/in/oliveirawilliandev/',

    -- [SQL-INGREDIENT-018] Código de barras (com valor padrão de 13 zeros)
                             bar_code VARCHAR(255) DEFAULT '0000000000000',

    -- =====================================================
    -- [SQL-INGREDIENT-019] RELACIONAMENTOS
    -- =====================================================

    -- [SQL-INGREDIENT-020] Fornecedor preferencial (opcional)
                             preferred_supplier_id BIGINT NOT NULL,

    -- =====================================================
    -- [SQL-INGREDIENT-021] CONSTRAINTS
    -- =====================================================

    -- [SQL-INGREDIENT-022] Nome do insumo deve ser único
                             CONSTRAINT uk_ingredients_name UNIQUE (name),

    -- [SQL-INGREDIENT-023] Chave estrangeira para fornecedor
                             CONSTRAINT fk_ingredients_supplier
                                 FOREIGN KEY (preferred_supplier_id)
                                     REFERENCES suppliers(id)
                                     ON DELETE SET NULL
);
