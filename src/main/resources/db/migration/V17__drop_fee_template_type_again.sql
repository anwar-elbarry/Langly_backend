-- Ensure legacy column is removed even if previous migration didn’t run
ALTER TABLE fee_templates
    DROP COLUMN IF EXISTS type;
