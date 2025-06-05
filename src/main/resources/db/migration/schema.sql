create type productType as enum ('plastic', 'metal', 'pnd');

create table customer
(
    id           bigserial primary key,
    tax_id       varchar(20)  not null unique,
    legal_name   varchar(255) not null,
    contact_info varchar(255)
);

create table warehouse
(
    id         bigserial primary key,
    gln        bigint      not null unique,
    short_name varchar(50) not null,
    address    text        not null,
    region     varchar     not null
);

create table product
(
    id          bigserial primary key,
    name        varchar(255) not null,
    barcode     varchar(13)  not null unique,
    coefficient numeric      not null,
    type        productType
);

create table "order"
(
    id            bigserial primary key,
    order_number  varchar(20) not null,
    issue_date    date not null,
    delivery_date date not null,
    customer_id   bigint references customer (id),
    warehouse_id  bigint references warehouse (id)
);

create table order_item
(
    id         bigserial primary key,
    order_id   bigint not null references "order" (id),
    product_id bigint not null references product (id),
    quantity   smallint not null check (quantity > 0),
    amount     decimal not null
);

create table invoice
(
    id             bigserial primary key,
    invoice_number smallint not null,
    invoice_date   date not null,
    sum            decimal not null,
    order_id       bigint references "order" (id)
);

