-- Align bank_info.id type with JPA expectation (String/VARCHAR)
ALTER TABLE bank_info
    ALTER COLUMN id TYPE VARCHAR(255) USING id::text;
