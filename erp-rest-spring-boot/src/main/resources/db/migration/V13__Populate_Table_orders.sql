CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,

                        employee_id BIGINT NOT NULL,
                        customer_id BIGINT NOT NULL,
                        total_amount NUMERIC(10,2) NOT NULL,
                        status VARCHAR(30) NOT NULL,

                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id)
                                REFERENCES customers(id),
                                CONSTRAINT fk_orders_employee
                                FOREIGN KEY (employee_id)
                                REFERENCES employees(id)
);