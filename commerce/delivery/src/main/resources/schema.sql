CREATE TABLE IF NOT EXISTS address (
    address_id UUID DEFAULT gen_random_UUID() PRIMARY KEY,
    country VARCHAR,
    city       VARCHAR,
    street     VARCHAR,
    house      VARCHAR,
    flat       VARCHAR
);

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID default gen_random_UUID() primary key,
    from_address UUID,
    to_address UUID,
    order_id UUID,
    delivery_state VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS from_address (
    delivery_id UUID REFERENCES deliveries (delivery_id),
    address_id UUID REFERENCES address (address_id)
);

CREATE TABLE IF NOT EXISTS to_address (
    delivery_id UUID REFERENCES deliveries (delivery_id),
    address_id UUID REFERENCES address (address_id)
);