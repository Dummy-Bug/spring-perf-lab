ALTER TABLE order_products
    ADD CONSTRAINT unique_order_products_order_product UNIQUE (order_id, product_id);

ALTER TABLE order_products
    DROP PRIMARY KEY,
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT FIRST,
    ADD PRIMARY KEY (id);

ALTER TABLE order_products
    ADD COLUMN quantity   INT         NULL,
    ADD COLUMN created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    ADD COLUMN updated_at DATETIME(6) NULL;