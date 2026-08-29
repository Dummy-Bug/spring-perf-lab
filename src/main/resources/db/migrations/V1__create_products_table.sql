CREATE TABLE products
(
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255)   NOT NULL,
    description TEXT,
    price       DECIMAL(38, 2) NOT NULL,
    image       VARCHAR(255),
    category    VARCHAR(255),
    rating      VARCHAR(255),
    PRIMARY KEY (id)
);