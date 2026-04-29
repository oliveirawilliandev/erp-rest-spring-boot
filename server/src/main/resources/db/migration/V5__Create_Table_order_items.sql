-- [SQL-ORDER-ITEM-001] Criação da tabela de itens do pedido
CREATE TABLE order_items (
    -- [SQL-ORDER-ITEM-002] Chave primária (auto-incremento)
                             id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-ORDER-ITEM-003] RELACIONAMENTOS E VALORES
    -- =====================================================

    -- [SQL-ORDER-ITEM-004] ID do pedido (obrigatório - FK para orders)
                             order_id BIGINT NOT NULL,

    -- [SQL-ORDER-ITEM-005] ID do produto (obrigatório - FK para products)
                             product_id BIGINT NOT NULL,

    -- [SQL-ORDER-ITEM-006] Quantidade do produto no pedido (obrigatório)
                             quantity INT NOT NULL,

    -- [SQL-ORDER-ITEM-007] Preço unitário no momento do pedido (obrigatório)
    -- Valor congelado para não ser alterado por mudanças futuras no preço do produto
                             unit_price NUMERIC(10,2) NOT NULL,

    -- =====================================================
    -- [SQL-ORDER-ITEM-008] CHAVES ESTRANGEIRAS
    -- =====================================================

    -- [SQL-ORDER-ITEM-009] Relacionamento com tabela orders
    -- ON DELETE CASCADE: se o pedido for excluído, os itens também são
                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id)
                                     ON DELETE CASCADE,  -- Remove itens automaticamente ao deletar pedido

    -- [SQL-ORDER-ITEM-010] Relacionamento com tabela products
    -- Sem ON DELETE CASCADE para não perder histórico de produtos deletados
                             CONSTRAINT fk_order_items_product
                                 FOREIGN KEY (product_id)
                                     REFERENCES products(id)
);