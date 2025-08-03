-- Создание новой схемы
DROP SCHEMA IF EXISTS logistics CASCADE;
CREATE SCHEMA logistics;
SET search_path TO logistics;

-- Функция для обновления временных меток
CREATE OR REPLACE FUNCTION update_modified_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Таблица групп товаров
CREATE TABLE product_groups
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE CHECK (name IN ('пластик', 'металл', 'ПНД'))
);

COMMENT ON TABLE product_groups IS 'Группы товаров для классификации';
COMMENT ON COLUMN product_groups.name IS 'Название группы товаров';

-- Таблица контрагентов
CREATE TABLE counteragents
(
    inn        VARCHAR(12) PRIMARY KEY,
    name       VARCHAR(255)            NOT NULL,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW() NOT NULL,
    is_deleted BOOLEAN   DEFAULT false NOT NULL
);

COMMENT ON TABLE counteragents IS 'Контрагенты-покупатели';
COMMENT ON COLUMN counteragents.inn IS 'ИНН контрагента (первичный ключ)';

-- Таблица складов
CREATE TABLE warehouses
(
    gln        VARCHAR(13) PRIMARY KEY,
    address    TEXT                    NOT NULL,
    region     VARCHAR(100)            NOT NULL,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW() NOT NULL,
    is_deleted BOOLEAN   DEFAULT false NOT NULL
);

COMMENT ON TABLE warehouses IS 'Склады отгрузки';
COMMENT ON COLUMN warehouses.gln IS 'GLN-код склада (уникальный идентификатор)';

-- Таблица товаров
CREATE TABLE products
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(255)            NOT NULL,
    internal_barcode    VARCHAR(50) UNIQUE      NOT NULL,
    external_barcode    VARCHAR(50),
    internal_sku        VARCHAR(50) UNIQUE      NOT NULL,
    external_sku        VARCHAR(50),
    packing_coefficient DOUBLE PRECISION        NOT NULL CHECK (packing_coefficient > 0),
    group_id            INTEGER                 NOT NULL REFERENCES product_groups (id),
    created_at          TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at          TIMESTAMP DEFAULT NOW() NOT NULL,
    is_deleted          BOOLEAN   DEFAULT false NOT NULL
);

COMMENT ON TABLE products IS 'Товарная номенклатура';
COMMENT ON COLUMN products.packing_coefficient IS 'Упаковочный коэффициент для расчета мест на паллете';

-- Таблица заказов
CREATE TABLE orders
(
    id               SERIAL PRIMARY KEY,
    number           VARCHAR(50)             NOT NULL,
    order_date       DATE                    NOT NULL,
    delivery_date    DATE                    NOT NULL,
    counteragent_inn VARCHAR(12) REFERENCES counteragents (inn) ON DELETE RESTRICT,
    warehouse_gln    VARCHAR(13) REFERENCES warehouses (gln) ON DELETE RESTRICT,
    pallet_count     INTEGER                 NOT NULL DEFAULT 1 CHECK (pallet_count > 0),
    created_at       TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at       TIMESTAMP DEFAULT NOW() NOT NULL,
    is_deleted       BOOLEAN   DEFAULT false NOT NULL,
    UNIQUE (number, order_date)
);

COMMENT ON TABLE orders IS 'Заказы покупателей';
COMMENT ON COLUMN orders.pallet_count IS 'Количество паллет в заказе (рассчитывается при создании)';

-- Таблица счетов
CREATE TABLE invoices
(
    id               SERIAL PRIMARY KEY,
    number           VARCHAR(50)             NOT NULL,
    issue_date       DATE                    NOT NULL,
    order_id         INTEGER UNIQUE REFERENCES orders (id) ON DELETE CASCADE,
    counteragent_inn VARCHAR(12) REFERENCES counteragents (inn) ON DELETE RESTRICT,
    created_at       TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at       TIMESTAMP DEFAULT NOW() NOT NULL,
    is_deleted       BOOLEAN   DEFAULT false NOT NULL,
    UNIQUE (number, issue_date)
);

COMMENT ON TABLE invoices IS 'Счета к заказам';
COMMENT ON COLUMN invoices.order_id IS 'Ссылка на заказ (1:1 отношение)';

-- Таблица товарных позиций в заказах
CREATE TABLE order_items
(
    id          SERIAL PRIMARY KEY,
    order_id    INTEGER                 NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id  INTEGER                 NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    quantity    INTEGER                 NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10, 2)          NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(12, 2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    created_at  TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at  TIMESTAMP DEFAULT NOW() NOT NULL,
    is_deleted  BOOLEAN   DEFAULT false NOT NULL
);

COMMENT ON TABLE order_items IS 'Позиции товаров в заказах';
COMMENT ON COLUMN order_items.total_price IS 'Вычисляемое поле: количество × цена за единицу';

-- Таблица паллет
CREATE TABLE pallets
(
    id         SERIAL PRIMARY KEY,
    order_id   INTEGER                 NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP DEFAULT NOW() NOT NULL,
    is_deleted BOOLEAN   DEFAULT false NOT NULL
);

COMMENT ON TABLE pallets IS 'Паллеты для комплектации заказов';

-- Таблица товаров на паллетах
CREATE TABLE pallet_items
(
    pallet_id     INTEGER NOT NULL REFERENCES pallets (id) ON DELETE CASCADE,
    order_item_id INTEGER NOT NULL REFERENCES order_items (id) ON DELETE RESTRICT,
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (pallet_id, order_item_id)
);

COMMENT ON TABLE pallet_items IS 'Распределение товарных позиций по паллетам';

-- Индексы для ускорения запросов
CREATE INDEX idx_orders_number ON orders (number);
CREATE INDEX idx_orders_delivery_date ON orders (delivery_date);
CREATE INDEX idx_invoices_number ON invoices (number);
CREATE INDEX idx_invoices_issue_date ON invoices (issue_date);
CREATE INDEX idx_products_barcode ON products (internal_barcode);
CREATE INDEX idx_order_items_order ON order_items (order_id);
CREATE INDEX idx_pallets_order ON pallets (order_id);
CREATE INDEX idx_pallet_items_pallet ON pallet_items (pallet_id);
CREATE INDEX idx_products_group ON products (group_id);

-- Применение триггеров обновления времени
CREATE TRIGGER update_counteragents_modtime
    BEFORE UPDATE
    ON counteragents
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_warehouses_modtime
    BEFORE UPDATE
    ON warehouses
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_products_modtime
    BEFORE UPDATE
    ON products
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_orders_modtime
    BEFORE UPDATE
    ON orders
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_invoices_modtime
    BEFORE UPDATE
    ON invoices
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_order_items_modtime
    BEFORE UPDATE
    ON order_items
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_pallets_modtime
    BEFORE UPDATE
    ON pallets
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

-- Представление для упаковочных листов
CREATE OR REPLACE VIEW packing_slip_view AS
SELECT o.number                                            AS order_number,
       o.order_date,
       o.delivery_date,
       o.pallet_count                                      AS total_pallets,
       w.gln                                               AS warehouse_gln,
       w.address                                           AS warehouse_address,
       w.region                                            AS warehouse_region,
       inv.number                                          AS invoice_number,
       inv.issue_date                                      AS invoice_date,
       p.id                                                AS pallet_id,
       ROW_NUMBER() OVER (PARTITION BY o.id ORDER BY p.id) AS pallet_number,
       prod.name                                           AS product_name,
       prod.internal_barcode                               AS product_barcode,
       prod.internal_sku                                   AS product_sku,
       pg.name                                             AS product_group,
       pi.quantity                                         AS quantity_on_pallet,
       oi.unit_price                                       AS product_price,
       (pi.quantity * oi.unit_price)                       AS total_price
FROM orders o
         JOIN warehouses w ON o.warehouse_gln = w.gln
         LEFT JOIN invoices inv ON o.id = inv.order_id
         JOIN pallets p ON o.id = p.order_id
         JOIN pallet_items pi ON p.id = pi.pallet_id
         JOIN order_items oi ON pi.order_item_id = oi.id
         JOIN products prod ON oi.product_id = prod.id
         JOIN product_groups pg ON prod.group_id = pg.id
WHERE o.is_deleted = false
  AND p.is_deleted = false;


COMMENT ON VIEW packing_slip_view IS 'Готовые упаковочные листы для печати';

-- -- Настройка прав доступа
-- CREATE ROLE logistics_admin WITH LOGIN PASSWORD 'AdminPass123!';
-- CREATE ROLE logistics_reader WITH LOGIN PASSWORD 'ReaderPass456!';
-- CREATE ROLE logistics_writer WITH LOGIN PASSWORD 'WriterPass789!';
--
-- GRANT USAGE ON SCHEMA logistics TO logistics_admin, logistics_reader, logistics_writer;
--
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA logistics TO logistics_admin;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA logistics TO logistics_admin;
-- GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA logistics TO logistics_admin;
--
-- GRANT SELECT ON ALL TABLES IN SCHEMA logistics TO logistics_reader;
-- GRANT SELECT ON packing_slip_view TO logistics_reader;
--
-- GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA logistics TO logistics_writer;
-- GRANT USAGE ON ALL SEQUENCES IN SCHEMA logistics TO logistics_writer;
-- GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA logistics TO logistics_writer;
--
-- REVOKE DELETE ON ALL TABLES IN SCHEMA logistics FROM logistics_writer;
--
-- ALTER DEFAULT PRIVILEGES IN SCHEMA logistics
--     GRANT SELECT ON TABLES TO logistics_reader;
--
-- ALTER DEFAULT PRIVILEGES IN SCHEMA logistics
--     GRANT SELECT, INSERT, UPDATE ON TABLES TO logistics_writer;
--
-- ALTER DEFAULT PRIVILEGES IN SCHEMA logistics
--     GRANT ALL PRIVILEGES ON TABLES TO logistics_admin;

-- Проверка целостности количества товаров на паллетах
CREATE OR REPLACE FUNCTION check_pallet_quantity()
    RETURNS TRIGGER AS
$$
DECLARE
    max_qty INTEGER;
BEGIN
    SELECT oi.quantity
    INTO max_qty
    FROM order_items oi
    WHERE oi.id = NEW.order_item_id;

    IF NEW.quantity > max_qty THEN
        RAISE EXCEPTION 'Количество на паллете (%) превышает заказанное (%)', NEW.quantity, max_qty;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER pallet_quantity_check
    BEFORE INSERT OR UPDATE
    ON pallet_items
    FOR EACH ROW
EXECUTE FUNCTION check_pallet_quantity();

-- Вставка тестовых групп товаров
INSERT INTO product_groups (name)
VALUES ('пластик'),
       ('металл'),
       ('ПНД');

-- Уведомление об успешном выполнении
DO
$$
    BEGIN
        RAISE NOTICE 'База данных успешно создана в схеме "logistics"';
        RAISE NOTICE 'Доступные роли: logistics_admin, logistics_reader, logistics_writer';
        RAISE NOTICE 'Используйте VIEW packing_slip_view для получения упаковочных листов';
    END
$$;