CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,

                          name VARCHAR(150) NOT NULL,
                          description TEXT,
                          price NUMERIC(10,2) NOT NULL,
                          starting_price NUMERIC(10,2) NOT NULL,
                          stock_quantity INT NOT NULL,

                          active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
