-- ============================================================
-- V3 : Phase 0 — Fondations pour US03 à US07
-- ============================================================

-- 1. Sessions : corriger les types + ajouter champs pour US03 & US05
ALTER TABLE sessions ALTER COLUMN duration_minutes TYPE INTEGER USING duration_minutes::INTEGER;
ALTER TABLE sessions ALTER COLUMN schedualed_at TYPE TIMESTAMP WITHOUT TIME ZONE USING schedualed_at::TIMESTAMP;
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS room VARCHAR(255);
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS meeting_link VARCHAR(500);
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS qr_token VARCHAR(255);
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS qr_expires_at TIMESTAMP WITHOUT TIME ZONE;

-- 2. Attendance : corriger le type de marked_at
ALTER TABLE attendance ALTER COLUMN marked_at TYPE TIMESTAMP WITHOUT TIME ZONE USING marked_at::TIMESTAMP;

-- 3. Certifications : enrichir pour US07
ALTER TABLE certifications ADD COLUMN IF NOT EXISTS issued_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE certifications ADD COLUMN IF NOT EXISTS pdf_url VARCHAR(500);
ALTER TABLE certifications ADD COLUMN IF NOT EXISTS course_id VARCHAR(255);
ALTER TABLE certifications ADD COLUMN IF NOT EXISTS school_id VARCHAR(255);
ALTER TABLE certifications ADD COLUMN IF NOT EXISTS digital_signature VARCHAR(500);

ALTER TABLE certifications
    ADD CONSTRAINT FK_CERTIFICATIONS_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);
ALTER TABLE certifications
    ADD CONSTRAINT FK_CERTIFICATIONS_ON_SCHOOL FOREIGN KEY (school_id) REFERENCES schools (id);

-- 4. Course materials : nouvelle table pour US06
CREATE TABLE course_materials
(
    id          VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    type        SMALLINT     NOT NULL,
    file_url    VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP WITHOUT TIME ZONE,
    course_id   VARCHAR(255),
    CONSTRAINT pk_course_materials PRIMARY KEY (id),
    CONSTRAINT FK_COURSE_MATERIALS_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id)
);

-- 5. Billings : lien Stripe + Enrollment pour US04
ALTER TABLE billings ADD COLUMN IF NOT EXISTS enrollment_id VARCHAR(255);
ALTER TABLE billings ADD COLUMN IF NOT EXISTS stripe_payment_intent_id VARCHAR(255);
ALTER TABLE billings ADD COLUMN IF NOT EXISTS stripe_checkout_session_id VARCHAR(255);
ALTER TABLE billings ADD COLUMN IF NOT EXISTS invoice_pdf_url VARCHAR(500);

ALTER TABLE billings
    ADD CONSTRAINT FK_BILLINGS_ON_ENROLLMENT FOREIGN KEY (enrollment_id) REFERENCES enrollment (id);
