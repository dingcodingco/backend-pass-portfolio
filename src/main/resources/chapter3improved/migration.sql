INSERT INTO ch3_improved_orders (
    id,
    member_id,
    order_number,
    status,
    order_date,
    total_amount,
    total_items,
    created_at,
    updated_at
)
SELECT
    o.id,
    o.member_id,
    o.order_number,
    o.status,
    o.order_date,
    o.total_amount,
    (SELECT COUNT(*) FROM ch3_order_items oi WHERE oi.order_id = o.id) as total_items,
    o.created_at,
    o.updated_at
FROM ch3_orders o;