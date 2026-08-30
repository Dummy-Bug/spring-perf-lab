ALTER TABLE categories
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE products
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE orders
    ADD COLUMN deleted_at DATETIME(6) NULL;

ALTER TABLE order_products
    ADD COLUMN deleted_at DATETIME(6) NULL;