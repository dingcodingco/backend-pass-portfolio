-- 회원 100만명 생성
INSERT INTO ch2_members (email, password, nickname, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1000000) */
    1 as n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT 
    CONCAT('user', LPAD(n, 7, '0'), '@test.com'),
    '$2a$10$iWPQvWHXRaJvPpZn1qzp3.GKBHXa9mFQXj4.Nz.yu.YyXJxvxXnwi',
    CONCAT('User', LPAD(n, 7, '0')),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY),
    NOW()
FROM numbers;

-- 상품 10000개 생성
INSERT INTO ch3_products (name, price, stock_quantity, category, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 10000) */
    1 as n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 10000
)
SELECT 
    CONCAT(
        CASE n DIV 2000
            WHEN 0 THEN 'ELECTRONICS_'
            WHEN 1 THEN 'CLOTHING_'
            WHEN 2 THEN 'BOOKS_'
            WHEN 3 THEN 'FOOD_'
            ELSE 'OTHERS_'
        END,
        MOD(n, 2000)
    ),
    FLOOR(100000 + RAND() * 900000),
    FLOOR(1 + RAND() * 1000),
    CASE n DIV 2000
        WHEN 0 THEN 'ELECTRONICS'
        WHEN 1 THEN 'CLOTHING'
        WHEN 2 THEN 'BOOKS'
        WHEN 3 THEN 'FOOD'
        ELSE 'OTHERS'
    END,
    NOW(),
    NOW()
FROM numbers;

-- 주문 50만개 생성
INSERT INTO ch3_orders (member_id, order_number, status, order_date, total_amount, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 500000) */
    1 as n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 500000
)
SELECT 
    1 + MOD(n, 1000000),  -- member_id (1~1000000)
    CONCAT('ORD-', LPAD(n, 8, '0')),
    CASE MOD(n, 4)
        WHEN 0 THEN 'COMPLETED'
        WHEN 1 THEN 'PROCESSING'
        WHEN 2 THEN 'CANCELLED'
        ELSE 'PENDING'
    END,
    DATE_SUB(NOW(), INTERVAL MOD(n, 365) DAY),
    50000 + MOD(n, 100000),
    NOW(),
    NOW()
FROM numbers;

-- 주문상품 150만개 생성 (주문당 평균 3개)
INSERT INTO ch3_order_items (order_id, product_id, quantity, price, created_at, updated_at)
WITH RECURSIVE numbers AS (
    SELECT /*+ SET_VAR(cte_max_recursion_depth = 1500000) */
    1 as n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1500000
)
SELECT 
    1 + MOD(n, 500000),    -- order_id (1~500000)
    1 + MOD(n, 10000),     -- product_id (1~10000)
    1 + MOD(n, 5),         -- quantity (1~5)
    10000 + MOD(n, 90000),
    NOW(),
    NOW()
FROM numbers;