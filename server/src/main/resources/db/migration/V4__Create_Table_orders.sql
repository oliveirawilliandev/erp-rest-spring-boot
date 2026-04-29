-- [SQL-ORDER-001] Criação da tabela de pedidos
CREATE TABLE orders (
    -- [SQL-ORDER-002] Chave primária (auto-incremento)
                        id BIGSERIAL PRIMARY KEY,

    -- =====================================================
    -- [SQL-ORDER-003] RELACIONAMENTOS E VALORES
    -- =====================================================

    -- [SQL-ORDER-004] Funcionário responsável pelo pedido (obrigatório)
                        employee_id BIGINT NOT NULL,

    -- [SQL-ORDER-005] Cliente que fez o pedido (obrigatório)
                        customer_id BIGINT NOT NULL,

    -- [SQL-ORDER-006] Valor total do pedido (obrigatório - 2 casas decimais)
                        total_amount NUMERIC(10,2) NOT NULL,

    -- [SQL-ORDER-007] Status atual do pedido (obrigatório)
    -- Valores possíveis: PENDING, PROCESSING, COMPLETED, CANCELLED
                        status VARCHAR(30) NOT NULL,

    -- =====================================================
    -- [SQL-ORDER-008] DATAS DE AUDITORIA
    -- =====================================================

    -- [SQL-ORDER-009] Data de criação do pedido
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- [SQL-ORDER-010] Data da última atualização do pedido
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- =====================================================
    -- [SQL-ORDER-011] CHAVES ESTRANGEIRAS
    -- =====================================================

    -- [SQL-ORDER-012] Relacionamento com tabela customers
                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id)
                                REFERENCES customers(id),

    -- [SQL-ORDER-013] Relacionamento com tabela employee
                        CONSTRAINT fk_orders_employee
                            FOREIGN KEY (employee_id)
                                REFERENCES employees(id),

    -- =====================================================
    -- [SQL-ORDER-014] CÓDIGOS DO PEDIDO
    -- =====================================================

    -- [SQL-ORDER-015] QR Code para consulta/rastreamento
                        qr_code VARCHAR(255) DEFAULT 'https://www.linkedin.com/in/oliveirawilliandev/',

    -- [SQL-ORDER-016] Código de barras do pedido
                        bar_code VARCHAR(255) DEFAULT '0000000000000'
);