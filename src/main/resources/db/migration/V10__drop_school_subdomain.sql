-- Remove unused sub-domain column and related uniqueness
ALTER TABLE schools
    DROP COLUMN IF EXISTS sub_domain;

-- Drop possible unique constraint/index if it was created separately
ALTER TABLE schools
    DROP CONSTRAINT IF EXISTS uk_schools_sub_domain;

DROP INDEX IF EXISTS uk_schools_sub_domain;
