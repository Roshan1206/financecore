create database finance_core;

create schema fc_auth;
create schema fc_account;
create schema fc_customer;
create schema fc_transaction;

set search_path to fc_customer;
create type customer_enum as enum ('INDIVIDUAL', 'BUSINESS');
create type kyc_enum as enum ('PENDING', 'VERIFIED', 'REJECTED');
create type risk_enum as enum ('LOW', 'MEDIUM', 'HIGH');

create table customer (
	customer_id BIGSERIAL primary key,
	customer_number VARCHAR(16) unique not null,
	first_name VARCHAR(50) not null,
	last_name VARCHAR(50) not null,
	email VARCHAR(50) unique not null,
	phone_number VARCHAR(20) unique not null,
	date_of_birth date not null,
	customer_type customer_enum not null default 'INDIVIDUAL',
	kyc_status kyc_enum not null default 'PENDING',
	risk_profile risk_enum not null default 'LOW',
	created_at date not null default now(),
	updated_at date not null default now()
);

alter table fc_customer.customer alter column created_at type timestamp;
alter table fc_customer.customer alter column updated_at type timestamp;
alter table fc_customer.customer add column is_active boolean;
alter table fc_customer.customer alter column customer_number set default 'CUST' || nextval('fc_customer.customer_customer_id_seq');

create type address_enum as enum ('HOME', 'WORK', 'MAILINNG');

create table address(
	address_id BIGSERIAL primary key,
	customer_id BIGSERIAL,
	address_type address_enum default 'HOME',
	street_address VARCHAR(100) not null,
	city VARCHAR(50) not null,
	state VARCHAR(50) not null,
	zip_code VARCHAR(10) not null,
	country VARCHAR(50) not null,
	is_primary boolean default false,
	constraint fk_customer
		foreign key (customer_id)
		references customer(customer_id)
		on delete cascade
);

--enforce only 1 primary address per user
create unique index unique_primary_address_per_user
	on address (customer_id)
	where is_primary = true;


create type document_type_enum as enum ('PASSPORT', 'DRIVING_LICENSE', 'UTILITY_BILL', 'AADHAR_CARD');
create type verification_status_enum as enum ('PENDING', 'VERIFIED', 'REJECTED');

create table customer_document (
	document_id BIGSERIAL primary key,
	customer_id BIGSERIAL,
	document_type document_type_enum not null,
	document_number VARCHAR(50) unique not null,
	file_path VARCHAR(200) not null,
	verification_status verification_status_enum default 'PENDING',
	uploaded_at timestamp not null default now(),
	constraint fk_customer
		foreign key (customer_id)
		references customer(customer_id)
		on delete cascade 
);

alter table fc_customer.address alter column customer_id type bigint;
alter table fc_customer.customer_document alter column customer_id type bigint;
set search_path to fc_account;

create type account_type_enum as enum ('CURRENT', 'SAVING', 'CREDIT', 'LOAN');
create type account_status_enum as enum ('ACTIVE', 'INACTIVE', 'CLOSE', 'SUSPENDED');

create table account_product (
	product_id BIGSERIAL primary key,
	product_name VARCHAR(20) not null unique,
	product_type account_type_enum not null default 'SAVING',
	interest_rate decimal(4,2),
	minimum_balance decimal(6,2) check (minimum_balance > 0),
	overdraft_limit decimal(10,2),
	monthly_fee decimal(10,2),
	features json,
	is_active boolean not null default true,
	created_at timestamp not null default now(),
	updated_at timestamp not null default now()
);
alter table fc_account.account_product alter column product_name type varchar(50);
alter table fc_account.account_product drop constraint account_product_minimum_balance_check;
alter table fc_account.account_product add constraint account_product_minimum_balance_check check (minimum_balance >= 0);

create table account (
	account_id BIGSERIAL primary key,
	account_number VARCHAR(20) not null unique,
	customer_id BIGSERIAL unique not null,
	product_id BIGSERIAL,
	account_status account_status_enum not null default 'ACTIVE',
	balance decimal(10,2) not null check(balance >= 0),
	available_balance decimal(10,2) not null check(available_balance >= 0),
	currency_code varchar(10),
	opened_at timestamp not null default now(),
	closed_at timestamp,
	last_activity_date date not null,
	constraint 
		fk_product foreign key (product_id)
			references account_product(product_id)		
);
alter table fc_account.account alter column account_number set default 'ACC' || nextval('fc_account.account_account_id_seq');

create table account_beneficiary (
	beneficiary_id BIGSERIAL primary key,
	account_id BIGSERIAL not null unique,
	benificiary_name VARCHAR(50) not null,
	relationship VARCHAR(20) not null,
	percentage decimal(5,2) not null check(percentage <= 100.00),
	contact_info json	
);

alter table account_beneficiary add constraint fk_account foreign key (account_id) references account(account_id);
alter table fc_account.account add column custom_interest_rate decimal(4,2);
alter table fc_account.account add column custom_minimum_balance decimal(4,2);
alter table fc_account.account add column custom_overdraft_limit decimal(10,2);
alter table fc_account.account_beneficiary rename column benificiary_name to beneficiary_name;
alter table fc_account.account_beneficiary alter column contact_info type jsonb;
alter table fc_account.account_product alter column features type jsonb;
alter table fc_account.account alter column customer_id type bigint;
alter table fc_account.account alter column product_id type bigint;
alter table fc_account.account_beneficiary alter column account_id type bigint;
alter table fc_account.account alter column custom_minimum_balance type decimal(10,2);
alter table fc_account.account_beneficiary drop constraint account_beneficiary_account_id_key;

set search_path to fc_transaction;

create type parent_category_enum as enum ('FOOD_DINING', 'TRANSPORTATION', 'SHOPPING', 'BILLS_UTILITIES', 'ENTERTAINMENT', 'HEALTHCARE', 'INCOME', 'TRANSFER', 'PERSONAL_CARE', 'EDUCATION', 'TRAVEL', 'INVESTMENT');
create table transaction_subcategory(
	subcategory_id BIGSERIAL primary key,
	subcategory_name VARCHAR(100) not null,
	parent_category parent_category_enum not null,
	is_active boolean,
	created_at timestamp not null default now()
);

create type transaction_enum as enum ('CREDIT', 'DEBIT', 'TRANSFER');
create type transaction_status_enum as enum ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED');
create type channel_enum as enum('ATM','ONLINE', 'MOBILE', 'UPI', 'BRANCH');
create table transaction(
	transaction_id BIGSERIAL primary key,
	transaction_reference VARCHAR(100) not null unique,
	from_account_id BIGSERIAL not null,
	to_account_id BIGSERIAL not null,
	transaction_type transaction_enum not null,
	amount decimal(10,2),
	currency_code VARCHAR(10) default 'INR',
	description VARCHAR(100),
	subcategory_id BIGSERIAL,
	transaction_status transaction_status_enum not null,
	transaction_date timestamp not null default now(),
	value_date date not null,
	channel channel_enum,
	reference_number VARCHAR(100) not null,
	merchant_info json,
	location json,
	created_at timestamp not null,
	updated_at timestamp not null default now(),
	constraint fk_subcategory
		foreign key(subcategory_id)
		references transaction_subcategory(subcategory_id)
);

create type limit_type_enum as enum ('DAILY', 'WEEKLY', 'MONTHLY', 'TRANSCATION');
alter type fc_transaction.limit_type_enum rename value 'TRANSACATION' to 'TRANSACTION';
create table transaction_limits(
	limit_id BIGSERIAL primary key,
	account_id BIGSERIAL not null,
	limit_type limit_type_enum,
	limit_amount decimal(10,2),
	used_amount decimal(10,2),
	reset_frequency VARCHAR(20),
	last_reset_date date,
	created_at timestamp not null,
	updated_at timestamp not null default now()	
);

alter table fc_transaction.transaction alter column merchant_info type jsonb;
alter table fc_transaction.transaction alter column location type jsonb;
alter table fc_transaction.transaction alter column to_account_id type bigint;
alter table fc_transaction.transaction alter column from_account_id type bigint;
alter table fc_transaction.transaction_limits alter column account_id type bigint;

ALTER SEQUENCE fc_account.account_account_id_seq RESTART WITH 10000000000;
ALTER TABLE fc_account.account ALTER COLUMN customer_id DROP DEFAULT;
alter table fc_account.account alter column product_id drop default;
alter table fc_account.account_beneficiary alter column account_id drop default;
alter sequence fc_account.account_beneficiary_beneficiary_id_seq restart with 1000000000;
alter sequence fc_account.account_product_product_id_seq restart with 1000000000;

alter table fc_customer.address alter column customer_id drop default;
alter sequence fc_customer.address_address_id_seq restart with 1000000000;
alter table fc_customer.customer_document alter column customer_id drop default;
alter sequence fc_customer.customer_customer_id_seq restart with 1000000000;
alter sequence fc_customer.customer_document_document_id_seq restart with 1000000000;

alter sequence fc_transaction.transaction_transaction_id_seq restart with 1000000000;
alter sequence fc_transaction.transaction_limits_limit_id_seq restart with 1000000000;
alter sequence fc_transaction.transaction_subcategory_subcategory_id_seq restart with 1000000000;
alter table fc_transaction.transaction alter column from_account_id drop default;
alter table fc_transaction.transaction alter column to_account_id drop default;
alter table fc_transaction.transaction_limits alter column account_id drop default;














