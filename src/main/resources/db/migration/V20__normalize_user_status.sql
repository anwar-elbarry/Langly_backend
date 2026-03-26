-- Normalize users.status so API never returns NULL and enum mapping is stable.
DO $$
DECLARE
    v_data_type text;
BEGIN
    SELECT data_type
    INTO v_data_type
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'users'
      AND column_name = 'status';

    -- Convert legacy numeric enum ordinals to string enum values.
    IF v_data_type IN ('smallint', 'integer', 'bigint') THEN
        ALTER TABLE users
            ALTER COLUMN status TYPE VARCHAR(20)
            USING (
                CASE status
                    WHEN 0 THEN 'ACTIVE'
                    WHEN 1 THEN 'SUSPENDED'
                    ELSE NULL
                END
            );
    END IF;

    UPDATE users
    SET status = 'ACTIVE'
    WHERE status IS NULL;

    ALTER TABLE users
        ALTER COLUMN status SET DEFAULT 'ACTIVE';

    ALTER TABLE users
        ALTER COLUMN status SET NOT NULL;
END $$;
