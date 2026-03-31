INSERT INTO orders (employee_id, customer_id, total_amount, status, created_at, updated_at)
VALUES
    (1, 1, 500.00, 'PENDING', now(), now()),
    (2, 2, 1200.00, 'COMPLETED', now(), now()),
    (3, 3, 750.00, 'CANCELLED', now(), now()),
    (4, 4, 300.00, 'PENDING', now(), now()),
    (5, 5, 1500.00, 'COMPLETED', now(), now()),
    (6, 6, 400.00, 'PENDING', now(), now()),
    (7, 7, 900.00, 'COMPLETED', now(), now()),
    (8, 8, 250.00, 'PENDING', now(), now()),
    (9, 9, 1100.00, 'COMPLETED', now(), now()),
    (10,10, 350.00, 'CANCELLED', now(), now()),
    (11,11, 450.00, 'COMPLETED', now(), now()),
    (12,12, 700.00, 'PENDING', now(), now());