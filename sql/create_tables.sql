CREATE TABLE IF NOT EXISTS products
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(64) UNIQUE NOT NULL,
    price     NUMERIC CHECK ( price > 0 ),
    quantity  INTEGER CHECK ( quantity > 0 ),
    available BOOLEAN
);

CREATE TABLE IF NOT EXISTS category_types
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS product_categories
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS order_statuses
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS order_details
(
    id           BIGSERIAL PRIMARY KEY,
    status_id    BIGINT REFERENCES order_statuses,
    total_amount NUMERIC CHECK ( total_amount > 0 )
);

CREATE TABLE IF NOT EXISTS product_to_category
(
    product_id  BIGINT REFERENCES products,
    category_id BIGINT REFERENCES product_categories
);

CREATE TABLE IF NOT EXISTS details_to_products
(
    order_details_id BIGINT REFERENCES order_details,
    product_id       BIGINT REFERENCES products
);

CREATE TABLE IF NOT EXISTS categories_to_types
(
    category_id BIGINT REFERENCES product_categories,
    type_id     BIGINT REFERENCES category_types
);
