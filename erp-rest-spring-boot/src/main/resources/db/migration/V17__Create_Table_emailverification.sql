CREATE TABLE email_verification (
                                    id BIGSERIAL PRIMARY KEY,
                                    email VARCHAR(255) NOT NULL,
                                    code VARCHAR(20) NOT NULL,
                                    created_at TIMESTAMP NOT NULL,
                                    expires_at TIMESTAMP NOT NULL,
                                    used BOOLEAN DEFAULT FALSE
);