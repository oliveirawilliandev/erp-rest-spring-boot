CREATE TABLE purchase_items (
                                id BIGSERIAL PRIMARY KEY,

                                purchase_id BIGINT NOT NULL,
                                product_id BIGINT NOT NULL,

                                quantity INT NOT NULL,
                                unit_price NUMERIC(10,2) NOT NULL,

                                CONSTRAINT fk_purchase_items_purchase
                                    FOREIGN KEY (purchase_id)
                                        REFERENCES purchases(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_purchase_items_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES products(id)
);