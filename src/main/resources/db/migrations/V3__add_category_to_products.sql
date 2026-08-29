ALTER TABLE products DROP COLUMN category;

ALTER TABLE products ADD COLUMN category_id BIGINT NOT NULL;

ALTER TABLE products ADD CONSTRAINT fk_product_category
FOREIGN KEY (category_id) REFERENCES categories (id);