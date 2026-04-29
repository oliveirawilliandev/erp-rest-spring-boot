INSERT INTO purchases (supplier_id, employee_id, total_amount, purchase_date, status)
VALUES
    (1,6,60.90, now(),'RECEIVED'),
    (2,5,150.00, now(),'PENDING'),
    (3,4,350.00, now(),'RECEIVED'),
    (4,3,50.00, now(),'CANCELLED'),
    (5,2,70.00, now(),'RECEIVED'),
    (6,1,120.00, now(),'PENDING');
