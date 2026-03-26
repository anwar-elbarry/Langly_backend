CREATE TABLE bank_info (
    id UUID PRIMARY KEY,
    bank_name VARCHAR(255) NOT NULL DEFAULT '',
    account_holder VARCHAR(255) NOT NULL DEFAULT '',
    iban VARCHAR(255) NOT NULL DEFAULT '',
    motive VARCHAR(255) NOT NULL DEFAULT '',
    note TEXT
);
