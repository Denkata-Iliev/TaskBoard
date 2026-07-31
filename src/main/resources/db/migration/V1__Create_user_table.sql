CREATE TABLE IF NOT EXISTS users(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email varchar(255) NOT NULL UNIQUE,
    password_hash varchar (255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);