ALTER TABLE ch3_orders
    ADD INDEX idx_order_date_status_amount (order_date, status, total_amount DESC);