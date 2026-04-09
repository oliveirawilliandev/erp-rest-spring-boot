CREATE TABLE purchases (
                           id BIGSERIAL PRIMARY KEY,

                           supplier_id BIGINT NOT NULL,
                           employee_id BIGINT NOT NULL,

                           total_amount NUMERIC(10,2) NOT NULL,
                           purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           status VARCHAR(30) NOT NULL,

                           CONSTRAINT fk_purchases_supplier
                               FOREIGN KEY (supplier_id)
                                   REFERENCES suppliers(id),

                           CONSTRAINT fk_purchases_employee
                               FOREIGN KEY (employee_id)
                                   REFERENCES employees(id)
);