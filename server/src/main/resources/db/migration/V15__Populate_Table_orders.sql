INSERT INTO orders (employee_id, customer_id, total_amount, status, created_at, updated_at)
VALUES
    (1, 6, 130.00, 'PENDING', now(), now()),
    (2, 5, 93.00, 'SHIPPED', now(), now()),
    (3, 4, 18.00, 'CANCELLED', now(), now()),
    (4, 3, 10.00, 'PENDING', now(), now()),
    (5, 2, 192.00, 'SHIPPED', now(), now()),
    (6, 1, 140.00, 'PENDING', now(), now());
