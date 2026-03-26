-- Normalize subscription currency to MAD (Stripe expects ISO code)
ALTER TABLE subscriptions
    ALTER COLUMN currency SET DEFAULT 'MAD';

UPDATE subscriptions
SET currency = 'MAD'
WHERE currency IS NULL OR TRIM(LOWER(currency)) IN ('dh', 'mad', '');
