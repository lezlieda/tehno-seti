SET search_path TO logistics;

COPY product_groups FROM 'db/migration/data/product_groups.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY counteragents FROM 'db/migration/data/counteragents.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY warehouses FROM 'db/migration/data/warehouses.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY products FROM 'db/migration/data/products.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY orders FROM 'db/migration/data/orders.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY invoices FROM 'db/migration/data/invoices.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY order_items FROM 'db/migration/data/order_items.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY pallets FROM 'db/migration/data/pallets.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');
COPY pallet_items FROM 'db/migration/data/pallet_items.tsv' WITH (FORMAT CSV, HEADER, DELIMITER E'\t');