create table account (
  account_number decimal(8,0),
  customer_number decimal(5,0),
  account_balance decimal(8,0),
  primary key (account_number)
);

create table account_seq (
  customer_number decimal(5,0),
  next_sequence decimal(8,0),
  primary key (customer_number)
);