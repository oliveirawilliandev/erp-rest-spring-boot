-- =====================================================
-- [SQL-PURCHASE-ITEM-014] POPULAÇÃO DA TABELA DE ITENS DA COMPRA
-- Versão: V27 (após a migração para ingredient_id)
-- =====================================================

-- Inserindo itens de compra (agora usando ingredient_id em vez de product_id)
INSERT INTO purchase_items (purchase_id, ingredient_id, quantity, unit_price)
VALUES
    -- Compra 1: Compra de insumos básicos (farinha, açúcar, fermento)
    (1, 1, 5, 4.50),   -- Farinha de Trigo Tipo 1 (5kg)
    (1, 2, 2, 6.00),   -- Farinha de Trigo Integral (2kg)
    (1, 7, 3, 3.80),   -- Açúcar Cristal (3kg)
    (1, 13, 1, 15.00), -- Fermento Biológico Seco (1 pacote)

    -- Compra 2: Compra de ovos e laticínios
    (2, 16, 10, 15.00),  -- Ovos Brancos (10 dúzias)
    (3, 33, 10, 35.00),  -- Carne Moída (10kg)
    (4, 26, 5, 10.00),   -- Queijo Mussarela (8kg)
    (5, 30, 10, 7.00),   -- Óleo de Soja (10 litros)
    (6, 38, 3, 40.00);   -- Chocolate ao Leite (3kg)