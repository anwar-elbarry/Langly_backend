-- Add gender column to students table
ALTER TABLE students ADD COLUMN IF NOT EXISTS gender VARCHAR(10);

-- Convert students.level from SMALLINT (ordinal) to VARCHAR (string)
ALTER TABLE students ADD COLUMN IF NOT EXISTS level_str VARCHAR(5);
UPDATE students SET level_str = CASE level
    WHEN 0 THEN 'A1'
    WHEN 1 THEN 'A2'
    WHEN 2 THEN 'B1'
    WHEN 3 THEN 'B2'
    WHEN 4 THEN 'C1'
    WHEN 5 THEN 'C2'
    ELSE NULL
END;
ALTER TABLE students DROP COLUMN IF EXISTS level;
ALTER TABLE students RENAME COLUMN level_str TO level;

-- Convert certifications.level from SMALLINT (ordinal) to VARCHAR (string)
ALTER TABLE certifications ADD COLUMN IF NOT EXISTS level_str VARCHAR(5);
UPDATE certifications SET level_str = CASE level
    WHEN 0 THEN 'A1'
    WHEN 1 THEN 'A2'
    WHEN 2 THEN 'B1'
    WHEN 3 THEN 'B2'
    WHEN 4 THEN 'C1'
    WHEN 5 THEN 'C2'
    ELSE NULL
END;
ALTER TABLE certifications DROP COLUMN IF EXISTS level;
ALTER TABLE certifications RENAME COLUMN level_str TO level;

-- Convert course.required_level from SMALLINT (ordinal) to VARCHAR (string)
ALTER TABLE course ADD COLUMN IF NOT EXISTS required_level_str VARCHAR(5);
UPDATE course SET required_level_str = CASE required_level
    WHEN 0 THEN 'A1'
    WHEN 1 THEN 'A2'
    WHEN 2 THEN 'B1'
    WHEN 3 THEN 'B2'
    WHEN 4 THEN 'C1'
    WHEN 5 THEN 'C2'
    ELSE NULL
END;
ALTER TABLE course DROP COLUMN IF EXISTS required_level;
ALTER TABLE course RENAME COLUMN required_level_str TO required_level;

-- Convert course.target_level from SMALLINT (ordinal) to VARCHAR (string)
ALTER TABLE course ADD COLUMN IF NOT EXISTS target_level_str VARCHAR(5);
UPDATE course SET target_level_str = CASE target_level
    WHEN 0 THEN 'A1'
    WHEN 1 THEN 'A2'
    WHEN 2 THEN 'B1'
    WHEN 3 THEN 'B2'
    WHEN 4 THEN 'C1'
    WHEN 5 THEN 'C2'
    ELSE NULL
END;
ALTER TABLE course DROP COLUMN IF EXISTS target_level;
ALTER TABLE course RENAME COLUMN target_level_str TO target_level;
