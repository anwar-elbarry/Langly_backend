-- V5: Convert enrollment.status, billings.status, billings.payment_method from ORDINAL (SMALLINT) to VARCHAR (STRING)
-- This is required because we're adding new enum values (PENDING_APPROVAL, APPROVED, REJECTED) to EnrollmentStatus
-- and switching all enums to @Enumerated(EnumType.STRING) for safety.

-- 1. Convert enrollment.status (current ordinals: 0=PASSED, 1=FAILED, 2=WITHDRAWN, 3=TRANSFERRED, 4=IN_PROGRESS)
ALTER TABLE enrollment ADD COLUMN IF NOT EXISTS status_str VARCHAR(20);
UPDATE enrollment SET status_str = CASE status
    WHEN 0 THEN 'PASSED'
    WHEN 1 THEN 'FAILED'
    WHEN 2 THEN 'WITHDRAWN'
    WHEN 3 THEN 'TRANSFERRED'
    WHEN 4 THEN 'IN_PROGRESS'
    ELSE 'IN_PROGRESS'
END;
ALTER TABLE enrollment DROP COLUMN IF EXISTS status;
ALTER TABLE enrollment RENAME COLUMN status_str TO status;

-- 2. Convert billings.status (current ordinals: 0=PAID, 1=PENDING, 2=OVERDUE, 3=CANCELLED)
ALTER TABLE billings ADD COLUMN IF NOT EXISTS status_str VARCHAR(20);
UPDATE billings SET status_str = CASE status
    WHEN 0 THEN 'PAID'
    WHEN 1 THEN 'PENDING'
    WHEN 2 THEN 'OVERDUE'
    WHEN 3 THEN 'CANCELLED'
    ELSE 'PENDING'
END;
ALTER TABLE billings DROP COLUMN IF EXISTS status;
ALTER TABLE billings RENAME COLUMN status_str TO status;

-- 3. Convert billings.payment_method (current ordinals: 0=CASH, 1=BANK_TRANSFER, 2=ONLINE_GATEWAY, 3=STRIPE)
ALTER TABLE billings ADD COLUMN IF NOT EXISTS payment_method_str VARCHAR(20);
UPDATE billings SET payment_method_str = CASE payment_method
    WHEN 0 THEN 'CASH'
    WHEN 1 THEN 'BANK_TRANSFER'
    WHEN 2 THEN 'ONLINE_GATEWAY'
    WHEN 3 THEN 'STRIPE'
    ELSE NULL
END;
ALTER TABLE billings DROP COLUMN IF EXISTS payment_method;
ALTER TABLE billings RENAME COLUMN payment_method_str TO payment_method;
