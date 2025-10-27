CREATE TABLE processed_images (
    id VARCHAR(36) PRIMARY KEY,
    file_path VARCHAR(255) NOT NULL UNIQUE,
    original_file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Optional: Add index on created_at for faster date-based queries
CREATE INDEX idx_processed_images_created_at ON processed_images(created_at);

-- Optional: Add comment for documentation
COMMENT ON TABLE processed_images IS 'Stores metadata for inverted/processed images';
COMMENT ON COLUMN processed_images.id IS 'UUID identifier for the processed image';
COMMENT ON COLUMN processed_images.file_path IS 'File system path to the processed image file';
COMMENT ON COLUMN processed_images.original_file_name IS 'Original name of the uploaded file';
COMMENT ON COLUMN processed_images.file_size IS 'Size of the processed image in bytes';
COMMENT ON COLUMN processed_images.created_at IS 'Timestamp when the image was processed';
