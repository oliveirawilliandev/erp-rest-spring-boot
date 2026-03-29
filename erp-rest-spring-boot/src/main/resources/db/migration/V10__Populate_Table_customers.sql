CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,

    -- Basic info
                           name VARCHAR(150) NOT NULL,
                           email VARCHAR(150) UNIQUE,
                           phone VARCHAR(20),
                           document VARCHAR(20) UNIQUE,

    -- Address
                           zip_code VARCHAR(10),
                           street VARCHAR(200),
                           street_number VARCHAR(20),
                           address_complement VARCHAR(100),
                           neighborhood VARCHAR(100),
                           city VARCHAR(100),
                           state CHAR(2),

                           active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);