CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.warehouse_products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    width DOUBLE PRECISION NOT NULL,
    height DOUBLE PRECISION NOT NULL,
    depth DOUBLE PRECISION NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    quantity BIGINT
);

CREATE TABLE IF NOT EXISTS bookings (
    shopping_cart_id UUID PRIMARY KEY,
    delivery_weight DOUBLE PRECISION NOT NULL,
    delivery_volume DOUBLE PRECISION NOT NULL,
    fragile BOOLEAN NOT NULL,
    order_id UUID
);

CREATE TABLE IF NOT EXISTS booking_products (
    shopping_cart_id UUID REFERENCES bookings (shopping_cart_id) ON DELETE CASCADE PRIMARY KEY,
    product_id UUID NOT NULL,
    quantity INTEGER
)