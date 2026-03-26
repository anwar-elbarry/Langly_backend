-- Remove TVA columns from invoices (TVA is now computed on-the-fly in overview only)
ALTER TABLE invoices DROP COLUMN IF EXISTS tva_rate;
ALTER TABLE invoices DROP COLUMN IF EXISTS tva_amount;
ALTER TABLE invoices DROP COLUMN IF EXISTS total_ttc;
ALTER TABLE invoices RENAME COLUMN subtotal TO total;
