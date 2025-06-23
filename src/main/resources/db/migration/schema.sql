create schema if not exists proto;

create type proto.productType as enum ('plastic', 'metal', 'pnd');

create table if not exists proto.customers
(
    id           bigserial primary key,
    tax_id       varchar(20)  not null unique,
    legal_name   varchar(255) not null,
    contact_info varchar(255)
);

create table if not exists proto.warehouses
(
    id         bigserial primary key,
    gln        bigint      not null unique,
    short_name varchar(50) not null,
    address    text        not null,
    region     varchar     not null
);

create table if not exists proto.products
(
    id          bigserial primary key,
    name        varchar(255) not null,
    barcode     varchar(13)  not null unique,
    coefficient numeric      not null,
    type        proto.productType
);

create table if not exists proto.orders
(
    id            bigserial primary key,
    order_number  varchar(20) not null,
    issue_date    date        not null,
    delivery_date date        not null,
    customer_id   bigint references proto.customers (id),
    warehouse_id  bigint references proto.warehouses (id)
);

create table if not exists proto.order_items
(
    id         bigserial primary key,
    order_id   bigint   not null references proto.orders (id),
    product_id bigint   not null references proto.products (id),
    quantity   smallint not null check (quantity > 0),
    amount     decimal  not null
);

create table if not exists proto.invoices
(
    id             bigserial primary key,
    invoice_number smallint not null,
    invoice_date   date     not null,
    sum            decimal  not null,
    order_id       bigint references proto.orders (id)
);

insert into proto.customers (tax_id, legal_name, contact_info)
values ('7802348846', 'ООО "СТД "Петрович"', '-'),
       ('7722753969', 'ООО "Всеинструменты.Ру"', '-'),
       ('4011024321', 'ООО Тест', 'test');


insert into proto.warehouses (gln, short_name, address, region)
values (4607977269995, 'Домодедово',
        '142032, Московская область, г. Домодедово, п. Госплемзавода Константиново, стр. 03', 'Москва'),
       (4630016670186, 'Новая Рига', '143421, Московская область, с.п. Ильинское, а/д "Балтия", 26 км, уч 3', 'Москва'),
       (4630016670261, 'Новорязанское ш.', '140105, Московская область, п. Томилино, Новорязанское ш., 23-й км, д.19А',
        'Москва'),
       (4630016670179, 'Горьковское ш.', '143912, Московская область, Балашиха, ш. Энтузиастов, вл.11, стр.2',
        'Москва'),
       (4630016670315, 'Симферопольское ш.',
        '142718, Московская область, Ленинский р-он, с/п Булатниковское, дер. Боброво, строение 67Ю', 'Москва'),
       (4630016670513, 'Дмитровское ш.',
        '141044, Московская область, городской округ Мытищи, д. Грибки, Дмитровское ш., строение 56/2', 'Москва'),
       (4630016670698, 'РЦ Дмитровское ш.',
        '141044, Московская область, г.о. Мытищи, д. Грибки, Дмитровское ш., стр. 56/2', 'Москва'),
       (4630016670674, 'РЦ Юкки', '194356, Санкт-Петербург, п.Парголово, Выборгское ш., 503к3с1, п.6-Н',
        'Санкт-Петербург'),
       (4630016670063, 'РЦ Новосаратовка',
        '193149, Ленинградская область, Всеволожский район, ГП им. Свердлова, д.Новосаратовка, Уткина заводь, Логистический центр МЛП, дом 15',
        'Санкт-Петербург'),
       (4630016670018, 'Индустриальный', '195253, Санкт-Петербург, Лапинский пр-т, 7', 'Санкт-Петербург'),
       (4630016670629, 'Мурманское ш. (2)',
        '188657, Ленинградская область, Всеволожский р-н, г.п. им.Свердлова, 12-13 км шоссе «Кола», д.3',
        'Санкт-Петербург'),
       (4630016670032, 'Парнас', '194358, Санкт-Петербург, Энгельса, 157А', 'Санкт-Петербург'),
       (4630016670056, 'Планерная', '197372, Санкт-Петербург, ул. Планерная, 15В', 'Санкт-Петербург'),
       (4630016670070, 'Славянка', '196627, Санкт-Петербург, п. Шушары, Ленсоветский тер., уч. 24', 'Санкт-Петербург'),
       (4630016670087, 'Софийская', '192241, Санкт-Петербург, Южное шоссе, д.39, лит. Т', 'Санкт-Петербург'),
       (4630016670230, 'Таллинское ш. (2)',
        '198205, Санкт-Петербург, МО Горелово, терр. Старо-Паново, Таллинское шоссе, 155к1, стр. 1', 'Санкт-Петербург'),
       (4630016670278, 'КАД Север',
        '188660, Ленинградская область, Всеволожский р-н, д. Порошкино, ул.Богородская, д.3', 'Санкт-Петербург'),
       (4630016670001, 'Гатчина', '188300, Ленинградская область, Гатчина, Промзона № 1, кв-л 6, пл.1',
        'Санкт-Петербург');

