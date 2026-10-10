CREATE TABLE orders (
  id UUID PRIMARY KEY,
  customer_id VARCHAR(100) NOT NULL,
  amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
  status VARCHAR(30) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE order_items (
  id UUID PRIMARY KEY,
  order_id UUID NOT NULL REFERENCES orders(id),
  product_id VARCHAR(100) NOT NULL,
  quantity INTEGER NOT NULL CHECK (quantity > 0)
);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE TABLE outbox_events (
  id UUID PRIMARY KEY,
  aggregate_id UUID NOT NULL,
  event_type VARCHAR(100) NOT NULL,
  topic VARCHAR(249) NOT NULL,
  message_key VARCHAR(200) NOT NULL,
  payload TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  published_at TIMESTAMPTZ,
  attempts INTEGER NOT NULL DEFAULT 0,
  next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  claimed_by VARCHAR(100),
  locked_until TIMESTAMPTZ,
  last_error VARCHAR(2000)
);
CREATE INDEX idx_outbox_pending ON outbox_events(next_attempt_at,created_at) WHERE published_at IS NULL;
