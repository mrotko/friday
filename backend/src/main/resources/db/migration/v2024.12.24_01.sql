create table fr_order
(
  id          numeric primary key not null,
  external_id varchar(255)        not null,
  order_date  timestamp           not null,
  amount      numeric             not null,
  currency    varchar(32)         not null,
  SOURCE      varchar(255)        not null
);
