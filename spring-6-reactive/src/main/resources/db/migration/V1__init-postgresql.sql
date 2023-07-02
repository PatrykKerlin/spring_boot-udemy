CREATE TABLE if NOT EXISTS beer
(
    id       SERIAL,
    version  integer,
    name     varchar(255),
    style    varchar(255),
    upc      varchar(25),
    quantity integer,
    price    decimal,
    created  timestamp,
    modified timestamp
);

CREATE TABLE if NOT EXISTS customer
(
    id       SERIAL,
    version  integer,
    name     varchar(255),
    created  timestamp,
    modified timestamp
);