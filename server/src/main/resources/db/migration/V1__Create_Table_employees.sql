CREATE TABLE employees (
                           id BIGSERIAL PRIMARY KEY,

    -- Personal data
                           first_name VARCHAR(150) NOT NULL,
                           last_name VARCHAR(150),
                           cpf VARCHAR(14) UNIQUE,
                           gender VARCHAR(20) NOT NULL,
                           birth_date DATE NOT NULL,

    -- Contact
                           email VARCHAR(150) UNIQUE,
                           phone VARCHAR(20),
                           mobile_phone VARCHAR(20),

    -- Address
                           zip_code VARCHAR(10),
                           street VARCHAR(200),
                           street_number VARCHAR(20),
                           address_complement VARCHAR(100),
                           neighborhood VARCHAR(100),
                           city VARCHAR(100),
                           state CHAR(2),

    -- Employment data
                           job_title VARCHAR(100) NOT NULL,   -- CARGO DO FUNCIONÁRIO
                           department VARCHAR(100),
                           hire_date DATE NOT NULL,
                           termination_date DATE,
                           active BOOLEAN DEFAULT TRUE,

    -- Audit
                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

  -- Photo / Bar Codes
                           photo_url VARCHAR(255) DEFAULT 'https://raw.githubusercontent.com/oliveirawilliandev/img/refs/heads/main/00_some_person.jpg',
                           qr_code VARCHAR(255) DEFAULT 'https://www.linkedin.com/in/oliveirawilliandev/',
                           bar_code VARCHAR(255) DEFAULT '0000000000000'

);
