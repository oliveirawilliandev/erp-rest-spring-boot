-- =====================================================
-- [SQL-PURCHASE-ITEM-001] RECRIAÇÃO DA TABELA DE ITENS DA COMPRA
-- Versão: V26  ingredient_id
-- =====================================================

CREATE TABLE purchase_items (
    -- [SQL-PURCHASE-ITEM-002] Chave primária (auto-incremento)
                                id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-PURCHASE-ITEM-003] RELACIONAMENTOS
    -- =====================================================

    -- [SQL-PURCHASE-ITEM-004] ID da compra (obrigatório - FK para purchases)
                                purchase_id BIGINT NOT NULL,

    -- [SQL-PURCHASE-ITEM-005] ID do insumo (obrigatório - FK para ingredients)
                                ingredient_id BIGINT NOT NULL,

    -- =====================================================
    -- [SQL-PURCHASE-ITEM-006] VALORES
    -- =====================================================

    -- [SQL-PURCHASE-ITEM-007] Quantidade comprada do insumo (obrigatório)
                                quantity INT NOT NULL,

    -- [SQL-PURCHASE-ITEM-008] Preço unitário no momento da compra (obrigatório)
    -- Valor congelado para não ser alterado por mudanças futuras
                                unit_price NUMERIC(10,2) NOT NULL,

    -- =====================================================
    -- [SQL-PURCHASE-ITEM-009] CHAVES ESTRANGEIRAS
    -- =====================================================

    -- [SQL-PURCHASE-ITEM-010] Relacionamento com tabela purchases
    -- ON DELETE CASCADE: se a compra for excluída, os itens também são
                                CONSTRAINT fk_purchase_items_purchase
                                    FOREIGN KEY (purchase_id)
                                        REFERENCES purchases(id)
                                        ON DELETE CASCADE,

    -- [SQL-PURCHASE-ITEM-011] Relacionamento com tabela ingredients
    -- ON DELETE RESTRICT: não permite deletar insumo que tem histórico de compra
                                CONSTRAINT fk_purchase_items_ingredient
                                    FOREIGN KEY (ingredient_id)
                                        REFERENCES ingredients(id)
                                        ON DELETE RESTRICT
);
