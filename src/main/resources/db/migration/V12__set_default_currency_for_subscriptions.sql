-- Ensure subscriptions have a default currency of 'DH'
ALTER TABLE subscriptions
    ALTER COLUMN currency SET DEFAULT 'DH';

UPDATE subscriptions
SET currency = 'DH'
WHERE currency IS NULL OR currency = '';
