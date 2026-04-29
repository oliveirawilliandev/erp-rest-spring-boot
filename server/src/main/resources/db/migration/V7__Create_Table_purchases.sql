-- [SQL-PURCHASE-001] Criação da tabela de compras (pedidos de compra)
CREATE TABLE purchases (
    -- [SQL-PURCHASE-002] Chave primária (auto-incremento)
                           id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-PURCHASE-003] RELACIONAMENTOS
    -- =====================================================

    -- [SQL-PURCHASE-004] Fornecedor da compra (obrigatório - FK para suppliers)
                           supplier_id BIGINT NOT NULL,

    -- [SQL-PURCHASE-005] Funcionário responsável pela compra (obrigatório - FK para employee)
                           employee_id BIGINT NOT NULL,

    -- =====================================================
    -- [SQL-PURCHASE-006] VALORES E DATAS
    -- =====================================================

    -- [SQL-PURCHASE-007] Valor total da compra (obrigatório - 2 casas decimais)
                           total_amount NUMERIC(10,2) NOT NULL,

    -- [SQL-PURCHASE-008] Data da compra (preenchida automaticamente)
                           purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- [SQL-PURCHASE-009] Status atual da compra (obrigatório)
    -- Valores possíveis: PENDING, APPROVED, SHIPPED, RECEIVED, CANCELLED
                           status VARCHAR(30) NOT NULL,

    -- =====================================================
    -- [SQL-PURCHASE-010] CHAVES ESTRANGEIRAS
    -- =====================================================

    -- [SQL-PURCHASE-011] Relacionamento com tabela suppliers
                           CONSTRAINT fk_purchases_supplier
                               FOREIGN KEY (supplier_id)
                                   REFERENCES suppliers(id),

    -- [SQL-PURCHASE-012] Relacionamento com tabela employee
                           CONSTRAINT fk_purchases_employee
                               FOREIGN KEY (employee_id)
                                   REFERENCES employees(id)
);