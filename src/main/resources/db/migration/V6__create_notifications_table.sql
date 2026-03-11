CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UNREAD',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recipient_id VARCHAR(255) NOT NULL REFERENCES users(id),
    reference_id VARCHAR(255),
    reference_type VARCHAR(50)
);

CREATE INDEX idx_notifications_recipient_status ON notifications(recipient_id, status);
