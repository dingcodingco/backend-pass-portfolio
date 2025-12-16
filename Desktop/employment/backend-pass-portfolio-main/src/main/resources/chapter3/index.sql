ALTER TABLE ch3_orders
    ADD INDEX idx_status (status),
    ADD INDEX idx_total_amount (total_amount);