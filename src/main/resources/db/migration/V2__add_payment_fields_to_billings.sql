-- US-AD-04 : Ajout de payment_method et paid_at sur la table billings
-- pour permettre la validation des paiements manuels (CASH / BANK_TRANSFER)

ALTER TABLE billings ADD COLUMN IF NOT EXISTS payment_method SMALLINT;
ALTER TABLE billings ADD COLUMN IF NOT EXISTS paid_at        TIMESTAMP WITHOUT TIME ZONE;
