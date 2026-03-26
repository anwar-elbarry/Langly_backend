-- Allow longer logo URLs and addresses for schools
ALTER TABLE schools
    ALTER COLUMN logo TYPE TEXT,
    ALTER COLUMN address TYPE TEXT;
