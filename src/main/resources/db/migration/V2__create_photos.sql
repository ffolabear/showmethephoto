CREATE TABLE photos
(
    id                UUID PRIMARY KEY,
    user_id           UUID         NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    storage_key       VARCHAR(100) NOT NULL UNIQUE,
    content_type      VARCHAR(100) NOT NULL,
    file_size         BIGINT       NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL,

    CONSTRAINT fk_photos_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_photos_user_created_at
    ON photos (user_id, created_at DESC);