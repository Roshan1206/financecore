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
alter table fc_customer.customer_document add column file_name varchar(100);
alter table 
alter table fc_customer.customer_document alter column file_name set not null;
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
create sequence account_number_seq start with 10000000000;
alter table fc_account.account alter column account_number set default nextval('account_number_seq');

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

-- V1__Create_all_tables.sql

-- User tables with BIGSERIAL (creates sequences automatically)
CREATE TABLE fc_auth.users (
	user_id BIGSERIAL NOT NULL,  -- Changed from int8 to BIGSERIAL
	email varchar(255) NOT NULL,
	first_name varchar(255) NOT NULL,
	last_name varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	enabled boolean DEFAULT true,
	account_non_expired boolean DEFAULT true,
	account_non_locked boolean DEFAULT true,
	credentials_non_expired boolean DEFAULT true,
	created_at timestamp,
	last_modified_at timestamp,
	CONSTRAINT users_email_key UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (user_id)
);

CREATE TABLE fc_auth.role (
	id BIGSERIAL NOT NULL,  -- Changed from int8 to BIGSERIAL
	role varchar(255) NOT NULL,
	CONSTRAINT role_pkey PRIMARY KEY (id),
	CONSTRAINT role_role_key UNIQUE (role)
);

CREATE TABLE fc_auth.user_roles (
	role_id int8 NOT NULL,
	user_id int8 NOT NULL,
	CONSTRAINT user_roles_pkey PRIMARY KEY (role_id, user_id),
	CONSTRAINT fk_user_is FOREIGN KEY (user_id) REFERENCES fc_auth.users(user_id),
	CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES fc_auth.role(id)
);

-- OAuth2 tables
CREATE TABLE fc_auth.oauth2_registered_client (
    id varchar(100) NOT NULL,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE fc_auth.oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE fc_auth.oauth2_authorization (
    id varchar(100) NOT NULL,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes TEXT DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_code_value TEXT DEFAULT NULL,
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata TEXT DEFAULT NULL,
    access_token_value TEXT DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata TEXT DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    oidc_id_token_value TEXT DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata TEXT DEFAULT NULL,
    refresh_token_value TEXT DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata TEXT DEFAULT NULL,
    user_code_value TEXT DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata TEXT DEFAULT NULL,
    device_code_value TEXT DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata TEXT DEFAULT NULL,
    PRIMARY KEY (id)
);

-- Insert test data
INSERT INTO fc_auth.role (role) VALUES ('USER'), ('ADMIN');

INSERT INTO fc_auth.users (email, first_name, last_name, password) VALUES 
('happy@test.com', 'Happy', 'User', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.'), -- password: password
('admin@test.com', 'Admin', 'User', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.');  -- password: password

-- Link users to roles (assuming user_id 1 = USER role, user_id 2 = ADMIN role)
INSERT INTO fc_auth.user_roles (user_id, role_id) VALUES 
(1, 1), -- happy@test.com gets USER role
(2, 1), -- admin@test.com gets USER role  
(2, 2); -- admin@test.com gets ADMIN role too














