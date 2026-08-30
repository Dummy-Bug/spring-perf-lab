CREATE TABLE order_products
(
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_products_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_order_products_product FOREIGN KEY (product_id) REFERENCES products (id)
);