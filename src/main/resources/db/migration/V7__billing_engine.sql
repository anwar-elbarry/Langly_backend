-- =============================================
-- V7: Billing Engine — new tables + data migration
-- =============================================

-- 1. billing_settings (one-to-one with school)
CREATE TABLE billing_settings (
    id                       VARCHAR(255) NOT NULL,
    school_id                VARCHAR(255) NOT NULL,
    tva_rate                 DECIMAL      NOT NULL DEFAULT 20.00,
    due_date_days            INTEGER      NOT NULL DEFAULT 0,
    default_installment_plan VARCHAR(20)  NOT NULL DEFAULT 'FULL',
    block_on_unpaid          BOOLEAN      NOT NULL DEFAULT FALSE,
    discount_enabled         BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_billing_settings PRIMARY KEY (id),
    CONSTRAINT uk_billing_settings_school UNIQUE (school_id),
    CONSTRAINT fk_billing_settings_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

-- 2. discounts (per-school discount catalog)
CREATE TABLE discounts (
    id         VARCHAR(255)   NOT NULL,
    school_id  VARCHAR(255)   NOT NULL,
    name       VARCHAR(255)   NOT NULL,
    type       VARCHAR(20)    NOT NULL,
    value      DECIMAL        NOT NULL,
    is_active  BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP      NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_discounts PRIMARY KEY (id),
    CONSTRAINT fk_discounts_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

CREATE INDEX idx_discounts_school_id ON discounts (school_id);

-- 3. fee_templates (per-school fee catalog)
CREATE TABLE fee_templates (
    id           VARCHAR(255)   NOT NULL,
    school_id    VARCHAR(255)   NOT NULL,
    name         VARCHAR(255)   NOT NULL,
    type         VARCHAR(20)    NOT NULL,
    amount       DECIMAL        NOT NULL,
    is_recurring BOOLEAN        NOT NULL DEFAULT FALSE,
    is_active    BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_fee_templates PRIMARY KEY (id),
    CONSTRAINT fk_fee_templates_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

CREATE INDEX idx_fee_templates_school_id ON fee_templates (school_id);

-- 4. invoices (structured billing)
CREATE TABLE invoices (
    id              VARCHAR(255)   NOT NULL,
    invoice_number  VARCHAR(50)    NOT NULL,
    student_id      VARCHAR(255)   NOT NULL,
    school_id       VARCHAR(255)   NOT NULL,
    enrollment_id   VARCHAR(255),
    subtotal        DECIMAL        NOT NULL,
    tva_rate        DECIMAL        NOT NULL,
    tva_amount      DECIMAL        NOT NULL,
    total_ttc       DECIMAL        NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'UNPAID',
    issued_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    due_date        DATE,
    CONSTRAINT pk_invoices PRIMARY KEY (id),
    CONSTRAINT uk_invoices_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoices_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_invoices_school FOREIGN KEY (school_id) REFERENCES schools (id),
    CONSTRAINT fk_invoices_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollment (id)
);

CREATE INDEX idx_invoices_school_id ON invoices (school_id);
CREATE INDEX idx_invoices_student_id ON invoices (student_id);
CREATE INDEX idx_invoices_status ON invoices (status);

-- 5. invoice_lines (line items)
CREATE TABLE invoice_lines (
    id              VARCHAR(255)   NOT NULL,
    invoice_id      VARCHAR(255)   NOT NULL,
    fee_template_id VARCHAR(255),
    description     VARCHAR(255)   NOT NULL,
    amount          DECIMAL        NOT NULL,
    CONSTRAINT pk_invoice_lines PRIMARY KEY (id),
    CONSTRAINT fk_invoice_lines_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id),
    CONSTRAINT fk_invoice_lines_fee_template FOREIGN KEY (fee_template_id) REFERENCES fee_templates (id)
);

CREATE INDEX idx_invoice_lines_invoice_id ON invoice_lines (invoice_id);

-- 6. payment_schedules (installment plans)
CREATE TABLE payment_schedules (
    id          VARCHAR(255)   NOT NULL,
    invoice_id  VARCHAR(255)   NOT NULL,
    installment INTEGER        NOT NULL,
    amount      DECIMAL        NOT NULL,
    due_date    DATE           NOT NULL,
    status      VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    paid_at     TIMESTAMP,
    CONSTRAINT pk_payment_schedules PRIMARY KEY (id),
    CONSTRAINT fk_payment_schedules_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id)
);

CREATE INDEX idx_payment_schedules_invoice_id ON payment_schedules (invoice_id);

-- =============================================
-- Data Migration: defaults for existing schools
-- =============================================

-- Insert default billing_settings for every existing school
INSERT INTO billing_settings (id, school_id, tva_rate, due_date_days, default_installment_plan, block_on_unpaid, discount_enabled)
SELECT
    gen_random_uuid()::VARCHAR,
    s.id,
    20.00,
    0,
    'FULL',
    FALSE,
    FALSE
FROM schools s
WHERE NOT EXISTS (
    SELECT 1 FROM billing_settings bs WHERE bs.school_id = s.id
);
