-- =============================================
-- V9: Add currency to billing_settings and create fee_payments table
-- =============================================

-- 1. Add currency to billing_settings
ALTER TABLE billing_settings
ADD COLUMN currency VARCHAR(255) NOT NULL DEFAULT 'MAD';

-- 2. Create fee_payments table
CREATE TABLE fee_payments (
    id VARCHAR(255) NOT NULL,
    school_id VARCHAR(255) NOT NULL,
    student_id VARCHAR(255) NOT NULL,
    fee_template_id VARCHAR(255) NOT NULL,
    amount DECIMAL NOT NULL,
    paid_at DATE NOT NULL,
    note VARCHAR(500),
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_fee_payments PRIMARY KEY (id),
    CONSTRAINT fk_fee_payments_school FOREIGN KEY (school_id) REFERENCES schools (id),
    CONSTRAINT fk_fee_payments_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_fee_payments_template FOREIGN KEY (fee_template_id) REFERENCES fee_templates (id)
);

CREATE INDEX idx_fee_payments_school_id ON fee_payments (school_id);
CREATE INDEX idx_fee_payments_student_id ON fee_payments (student_id);
