-- 8. Insert Transactions (10 rows) - India-specific with INR currency
INSERT INTO transaction (transaction_reference, from_account_id, to_account_id, transaction_type, amount, currency_code, description, subcategory_id, transaction_status, transaction_date, value_date, channel, reference_number, merchant_info, location, created_at, updated_at) VALUES
('TXN2024090100001', 1, 0, 'DEBIT', 850.00, 'INR', 'Dinner at Indian Restaurant', 1, 'COMPLETED', '2024-09-01 19:30:00', '2024-09-01', 'UPI', 'UPI001', '{"merchant_name": "Spice Garden Restaurant", "upi_id": "spicegarden@paytm", "category": "Restaurant"}', '{"lat": 22.7196, "lon": 75.8577, "city": "Indore", "state": "Madhya Pradesh"}', NOW(), NOW()),
('TXN2024090100002', 2, 0, 'DEBIT', 2450.50, 'INR', 'Weekly grocery shopping', 2, 'COMPLETED', '2024-09-02 10:15:00', '2024-09-02', 'MOBILE', 'MOB002', '{"merchant_name": "Reliance Fresh", "category": "Grocery", "payment_method": "PhonePe"}', '{"lat": 22.7196, "lon": 75.8577, "city": "Indore", "state": "Madhya Pradesh"}', NOW(), NOW()),
('TXN2024090100003', 3, 0, 'DEBIT', 1200.00, 'INR', 'Petrol fill-up', 3, 'COMPLETED', '2024-09-03 08:45:00', '2024-09-03', 'ONLINE', 'HP003', '{"merchant_name": "HP Petrol Pump", "category": "Fuel", "payment_method": "Debit Card"}', '{"lat": 22.7281, "lon": 75.8677, "city": "Indore", "state": "Madhya Pradesh"}', NOW(), NOW()),
('TXN2024090100004', 0, 4, 'CREDIT', 85000.00, 'INR', 'Monthly salary deposit', 9, 'COMPLETED', '2024-09-01 00:01:00', '2024-09-01', 'ONLINE', 'SAL004', '{"source": "TechCorp Solutions Pvt Ltd", "type": "NEFT", "salary_month": "September 2024"}', '{"city": "Indore", "state": "Madhya Pradesh"}', NOW(), NOW()),
('TXN2024090100005', 4, 5, 'TRANSFER', 25000.00, 'INR', 'Transfer to business account', 10, 'COMPLETED', '2024-09-04 14:20:00', '2024-09-04', 'UPI', 'UPI005', '{"transfer_type": "UPI Transfer", "upi_id": "techcorp@okaxis"}', '{"online": true}', NOW(), NOW()),
('TXN2024090100006', 5, 0, 'DEBIT', 15999.00, 'INR', 'Online electronics purchase', 5, 'COMPLETED', '2024-09-05 16:30:00', '2024-09-05', 'ONLINE',-- Dummy data for Finance Core Database
-- Note: Insert data in this order to maintain foreign key relationships

-- 1. Insert Account Products (10 rows)
INSERT INTO fc_account.account_product (product_name, product_type, interest_rate, minimum_balance, overdraft_limit, monthly_fee, features, is_active, created_at, updated_at) VALUES
('Premium Savings Plus', 'SAVING', 4.50, 5000.00, 0.00, 0.00, '{"debit_card": true, "online_banking": true, "mobile_banking": true, "atm_withdrawals": "unlimited"}', true, NOW(), NOW()),
('Basic Checking Account', 'CURRENT', 0.50, 500.00, 1000.00, 5.00, '{"debit_card": true, "checks": true, "online_banking": true, "overdraft_protection": false}', true, NOW(), NOW()),
('Student Savings Account', 'SAVING', 3.75, 100.00, 0.00, 0.00, '{"no_monthly_fees": true, "mobile_banking": true, "student_benefits": true}', true, NOW(), NOW()),
('Premium Checking Plus', 'CURRENT', 1.25, 2500.00, 5000.00, 15.00, '{"premium_debit_card": true, "unlimited_transactions": true, "concierge_service": true}', true, NOW(), NOW()),
('Business Credit Line', 'CREDIT', 12.99, 1.00, 5000.00, 25.00, '{"business_rewards": true, "expense_tracking": true, "higher_limits": true}', true, NOW(), NOW()),
('Personal Credit Card', 'CREDIT', 18.99, 1.00, 10000.00, 0.00, '{"cashback_rewards": true, "fraud_protection": true, "contactless_payments": true}', true, NOW(), NOW()),
('Home Mortgage Loan', 'LOAN', 6.50, 1.00, 0.00, 0.00, '{"fixed_rate": true, "30_year_term": true, "online_payments": true}', true, NOW(), NOW()),
('Auto Loan Standard', 'LOAN', 5.75, 1.00, 0.00, 0.00, '{"competitive_rates": true, "flexible_terms": true, "early_payment_option": true}', true, NOW(), NOW()),
('High Yield Savings', 'SAVING', 5.25, 1000.00, 1.00, 0.00, '{"high_interest": true, "compound_daily": true, "no_penalty_withdrawals": 6}', true, NOW(), NOW()),
('Youth Checking Account', 'CURRENT', 0.25, 1.00, 0.00, 0.00, '{"parental_controls": true, "educational_resources": true, "no_fees_under_18": true}', false, NOW(), NOW());

-- 2. Insert Accounts (10 rows)
INSERT INTO fc_account.account (account_number, customer_id, product_id, account_status, balance, available_balance, currency_code, custom_interest_rate, custom_minimum_balance, custom_overdraft_limit, opened_at, closed_at, last_activity_date) VALUES
('ACC001234567890', 1001, 1000000017, 'ACTIVE', 15750.50, 15750.50, 'INR', NULL, NULL, NULL, '2023-01-15 10:30:00', NULL, CURRENT_DATE),
('ACC001234567891', 1002, 1000000018, 'ACTIVE', 3250.75, 2250.75, 'INR', NULL, NULL, 2000.00, '2023-02-20 14:15:00', NULL, CURRENT_DATE),
('ACC001234567892', 1003, 1000000019, 'ACTIVE', 850.25, 850.25, 'INR', 4.00, NULL, NULL, '2023-03-10 09:45:00', NULL, CURRENT_DATE - 1),
('ACC001234567893', 1004, 1000000020, 'ACTIVE', 45000.00, 40000.00, 'INR', NULL, NULL, 7500.00, '2022-12-05 16:20:00', NULL, CURRENT_DATE),
('ACC001234567894', 1005, 1000000021, 'ACTIVE', 2500.00, 47500.00, 'INR', 11.99, NULL, NULL, '2023-05-18 11:00:00', NULL, CURRENT_DATE - 2),
('ACC001234567895', 1006, 1000000022, 'ACTIVE', 750.00, 9250.00, 'INR', NULL, NULL, NULL, '2023-06-22 13:30:00', NULL, CURRENT_DATE - 3),
('ACC001234567896', 1007, 1000000023, 'ACTIVE', 185000.00, 0.00, 'INR', 6.25, NULL, NULL, '2022-08-30 10:00:00', NULL, CURRENT_DATE - 5),
('ACC001234567897', 1008, 1000000024, 'ACTIVE', 15000.00, 0.00, 'INR', NULL, NULL, NULL, '2023-04-12 15:45:00', NULL, CURRENT_DATE - 1),
('ACC001234567898', 1009, 1000000025, 'SUSPENDED', 25500.00, 0.00, 'INR', NULL, 15000.00, NULL, '2023-07-01 12:15:00', NULL, CURRENT_DATE - 10),
('ACC001234567899', 1010, 1000000026, 'CLOSE', 0.00, 0.00, 'INR', NULL, NULL, NULL, '2023-01-25 08:30:00', '2023-11-15 14:00:00', '2023-11-15');

-- 3. Insert Account Beneficiaries (10 rows)
INSERT INTO fc_account.account_beneficiary (account_id, beneficiary_name, relationship, percentage, contact_info) VALUES
(10000000010, 'Sarah Johnson', 'Spouse', 50.00, '{"phone": "+1-555-0101", "email": "sarah.j@email.com", "address": "123 Main St, Springfield, IL"}'),
(10000000010, 'Michael Johnson', 'Son', 30.00, '{"phone": "+1-555-0102", "email": "mike.j@email.com"}'),
(10000000010, 'Emma Johnson', 'Daughter', 20.00, '{"phone": "+1-555-0103", "email": "emma.j@email.com"}'),
(10000000011, 'Robert Smith', 'Father', 60.00, '{"phone": "+1-555-0201", "email": "robert.smith@email.com"}'),
(10000000011, 'Maria Smith', 'Mother', 40.00, '{"phone": "+1-555-0202", "email": "maria.smith@email.com"}'),
(10000000012, 'Jennifer Davis', 'Sister', 100.00, '{"phone": "+1-555-0301", "email": "jen.davis@email.com"}'),
(10000000013, 'David Wilson', 'Brother', 50.00, '{"phone": "+1-555-0401", "email": "d.wilson@email.com"}'),
(10000000013, 'Lisa Wilson', 'Sister-in-law', 50.00, '{"phone": "+1-555-0402", "email": "lisa.wilson@email.com"}'),
(10000000014, 'Thomas Brown', 'Father', 75.00, '{"phone": "+1-555-0501", "email": "tom.brown@email.com"}'),
(10000000020, 'Amanda Taylor', 'Spouse', 100.00, '{"phone": "+1-555-0601", "email": "amanda.taylor@email.com"}');

-- 4. Insert Transaction Categories (10 rows)
INSERT INTO fc_transaction.transaction_subcategory (subcategory_name, parent_category, is_active, created_at) VALUES
('Restaurants', 'FOOD_DINING', true, NOW()),
('Groceries', 'FOOD_DINING', true, NOW()),
('Gas Stations', 'TRANSPORTATION', true, NOW()),
('Public Transport', 'TRANSPORTATION', true, NOW()),
('Online Shopping', 'SHOPPING', true, NOW()),
('Clothing Stores', 'SHOPPING', true, NOW()),
('Electricity Bill', 'BILLS_UTILITIES', true, NOW()),
('Internet Bill', 'BILLS_UTILITIES', true, NOW()),
('Salary Deposit', 'INCOME', true, NOW()),
('Account Transfer', 'TRANSFER', true, NOW());

-- 5. Insert Transactions (10 rows)
INSERT INTO fc_transaction.transaction (transaction_reference, from_account_id, to_account_id, transaction_type, amount, currency_code, description, subcategory_id, transaction_status, transaction_date, value_date, channel, reference_number, merchant_info, location, created_at, updated_at) VALUES
('TXN2024090100001', 10000000011, 10000000010, 'DEBIT', 45.75, 'INR', 'Dinner at Italian Restaurant', 1000000000, 'COMPLETED', '2024-09-01 19:30:00', '2024-09-01', 'MOBILE', 'REF001', '{"merchant_name": "Mama Mia Restaurant", "category": "Restaurant"}', '{"lat": 39.7817, "lon": -89.6501, "city": "Springfield", "state": "IL"}', NOW(), NOW()),
('TXN2024090100002', 10000000012, 10000000010, 'DEBIT', 125.50, 'INR', 'Weekly grocery shopping', 1000000001, 'COMPLETED', '2024-09-02 10:15:00', '2024-09-02', 'ONLINE', 'REF002', '{"merchant_name": "FreshMart Groceries", "category": "Grocery"}', '{"lat": 39.7901, "lon": -89.6440, "city": "Springfield", "state": "IL"}', NOW(), NOW()),
('TXN2024090100003', 10000000013, 10000000010, 'DEBIT', 65.00, 'INR', 'Gas fill-up', 1000000002, 'COMPLETED', '2024-09-03 08:45:00', '2024-09-03', 'ATM', 'REF003', '{"merchant_name": "Shell Gas Station", "category": "Gas Station"}', '{"lat": 39.7956, "lon": -89.6439, "city": "Springfield", "state": "IL"}', NOW(), NOW()),
('TXN2024090100004', 10000000010, 10000000013, 'CREDIT', 4500.00, 'INR', 'Monthly salary deposit', 1000000008, 'COMPLETED', '2024-09-01 00:01:00', '2024-09-01', 'ONLINE', 'REF004', '{"source": "ABC Corp Payroll", "type": "Direct Deposit"}', '{"city": "Springfield", "state": "IL"}', NOW(), NOW()),
('TXN2024090100005', 10000000013, 10000000014, 'TRANSFER', 1000.00, 'INR', 'Transfer to credit account', 1000000009, 'COMPLETED', '2024-09-04 14:20:00', '2024-09-04', 'MOBILE', 'REF005', '{"transfer_type": "Internal Transfer"}', '{"online": true}', NOW(), NOW()),
('TXN2024090100006', 10000000014, 10000000010, 'DEBIT', 299.99, 'INR', 'Online electronics purchase', 1000000005, 'COMPLETED', '2024-09-05 16:30:00', '2024-09-05', 'ONLINE', 'REF006', '{"merchant_name": "TechWorld Online", "category": "Electronics"}', '{"online": true, "shipping_address": "Springfield, IL"}', NOW(), NOW()),
('TXN2024090100007', 10000000020, 10000000010, 'DEBIT', 15.00, 'INR', 'Bus fare', 1000000003, 'COMPLETED', '2024-09-06 07:30:00', '2024-09-06', 'MOBILE', 'REF007', '{"merchant_name": "City Transit", "category": "Public Transport"}', '{"lat": 39.8017, "lon": -89.6439, "city": "Springfield", "state": "IL"}', NOW(), NOW()),
('TXN2024090100008', 10000000021, 10000000010, 'DEBIT', 850.00, 'INR', 'Monthly mortgage payment', 1000000006, 'PENDING', '2024-09-07 09:00:00', '2024-09-07', 'ONLINE', 'REF008', '{"merchant_name": "First National Bank", "category": "Mortgage"}', '{"online": true}', NOW(), NOW()),
('TXN2024090100009', 10000000012, 10000000010, 'DEBIT', 75.99, 'INR', 'Internet bill payment', 1000000007, 'COMPLETED', '2024-09-08 11:15:00', '2024-09-08', 'ONLINE', 'REF009', '{"merchant_name": "SpeedNet ISP", "category": "Utilities"}', '{"online": true}', NOW(), NOW()),
('TXN2024090100010', 10000000011, 10000000010, 'DEBIT', 89.50, 'INR', 'Clothing purchase', 1000000005, 'FAILED', '2024-09-09 15:45:00', '2024-09-09', 'MOBILE', 'REF010', '{"merchant_name": "Fashion Hub", "category": "Clothing"}', '{"lat": 39.7885, "lon": -89.6501, "city": "Springfield", "state": "IL"}', NOW(), NOW());

-- 9. Insert Transaction Limits (10 rows) - Updated amounts for INR
INSERT INTO fc_transaction.transaction_limits (account_id, limit_type, limit_amount, used_amount, reset_frequency, last_reset_date, created_at, updated_at) VALUES
(10000000011, 'DAILY', 50000.00, 3300.50, 'DAILY', CURRENT_DATE, NOW(), NOW()),
(10000000011, 'MONTHLY', 200000.00, 18849.50, 'MONTHLY', '2024-09-01', NOW(), NOW()),
(10000000012, 'DAILY', 25000.00, 4849.50, 'DAILY', CURRENT_DATE, NOW(), NOW()),
(10000000012, 'WEEKLY', 100000.00, 27299.50, 'WEEKLY', '2024-09-02', NOW(), NOW()),
(10000000013, 'DAILY', 15000.00, 1200.00, 'DAILY', CURRENT_DATE, NOW(), NOW()),
(10000000013, 'TRANSACTION', 10000.00, 0.00, 'PER_TRANSACTION', CURRENT_DATE, NOW(), NOW()),
(10000000014, 'DAILY', 100000.00, 0.00, 'DAILY', CURRENT_DATE, NOW(), NOW()),
(10000000014, 'MONTHLY', 500000.00, 110000.00, 'MONTHLY', '2024-09-01', NOW(), NOW()),
(10000000015, 'DAILY', 200000.00, 40999.00, 'DAILY', CURRENT_DATE, NOW(), NOW()),
(10000000016, 'WEEKLY', 50000.00, 2844.00, 'WEEKLY', '2024-09-02', NOW(), NOW());

-- Sample data for fc_customer.customer table (India Region)
INSERT INTO fc_customer.customer (customer_id, customer_number, first_name, last_name, email, phone_number, date_of_birth, customer_type, kyc_status, risk_profile, created_at, updated_at, is_active) VALUES
(1002, 'CUST1002', 'Rajesh', 'Sharma', 'rajesh.sharma@gmail.com', '+91-9876543210', '1985-03-15', 'INDIVIDUAL', 'VERIFIED', 'MEDIUM', '2024-01-15 10:30:00', '2024-01-15 10:30:00', true),
(1003, 'CUST1003', 'Priya', 'Gupta', 'priya.gupta@yahoo.in', '+91-8765432109', '1990-07-22', 'INDIVIDUAL', 'VERIFIED', 'LOW', '2024-01-16 11:45:00', '2024-01-16 11:45:00', true),
(1004, 'CUST1004', 'Amit', 'Patel', 'amit.patel@hotmail.com', '+91-7654321098', '1978-12-08', 'INDIVIDUAL', 'VERIFIED', 'HIGH', '2024-01-17 09:20:00', '2024-01-17 09:20:00', true),
(1005, 'CUST1005', 'Sneha', 'Singh', 'sneha.singh@rediffmail.com', '+91-6543210987', '1992-05-30', 'INDIVIDUAL', 'VERIFIED', 'LOW', '2024-01-18 14:15:00', '2024-01-18 14:15:00', true),
(1006, 'CUST1006', 'Vikram', 'Kumar', 'vikram.kumar@outlook.in', '+91-5432109876', '1983-09-14', 'INDIVIDUAL', 'VERIFIED', 'MEDIUM', '2024-01-19 16:30:00', '2024-01-19 16:30:00', true),
(1007, 'CUST1007', 'Anita', 'Joshi', 'anita.joshi@gmail.com', '+91-4321098765', '1987-11-03', 'INDIVIDUAL', 'PENDING', 'LOW', '2024-01-20 08:45:00', '2024-01-20 08:45:00', true),
(1008, 'CUST1008', 'Suresh', 'Reddy', 'suresh.reddy@yahoo.in', '+91-3210987654', '1975-04-18', 'INDIVIDUAL', 'VERIFIED', 'HIGH', '2024-01-21 13:20:00', '2024-01-21 13:20:00', true),
(1009, 'CUST1009', 'Meera', 'Nair', 'meera.nair@gmail.com', '+91-2109876543', '1989-08-25', 'INDIVIDUAL', 'VERIFIED', 'MEDIUM', '2024-01-22 10:10:00', '2024-01-22 10:10:00', true),
(1010, 'CUST1010', 'InfoTech Solutions Pvt Ltd', 'Company', 'admin@infotechsolutions.co.in', '+91-1098765432', '2010-01-01', 'BUSINESS', 'VERIFIED', 'LOW', '2024-01-23 15:30:00', '2024-01-23 15:30:00', true),
(1001, 'CUST1001', 'Arjun', 'Agarwal', 'arjun.agarwal@hotmail.com', '+91-9087654321', '1982-06-12', 'INDIVIDUAL', 'VERIFIED', 'MEDIUM', '2024-01-24 12:00:00', '2024-01-24 12:00:00', true);

-- Sample data for fc_customer.address table (India Region)
INSERT INTO fc_customer.address (customer_id, address_type, street_address, city, state, zip_code, country, is_primary) VALUES
(1002, 'HOME', '101, Shanti Nagar, Sector 15', 'Mumbai', 'Maharashtra', '400001', 'India', true),
(1003, 'HOME', '205, Green Park Extension', 'New Delhi', 'Delhi', '110016', 'India', true),
(1004, 'HOME', '78, MG Road, Koramangala', 'Bangalore', 'Karnataka', '560034', 'India', true),
(1005, 'HOME', '45, Anna Salai, T Nagar', 'Chennai', 'Tamil Nadu', '600017', 'India', true),
(1006, 'HOME', '12, Salt Lake City, Sector V', 'Kolkata', 'West Bengal', '700091', 'India', true),
(1007, 'HOME', '301, Banjara Hills, Road No 3', 'Hyderabad', 'Telangana', '500034', 'India', true),
(1008, 'HOME', '67, Jubilee Hills, Film Nagar', 'Hyderabad', 'Telangana', '500033', 'India', true),
(1009, 'HOME', '23, Marine Drive, Fort Kochi', 'Kochi', 'Kerala', '682001', 'India', true),
(1010, 'WORK', '5th Floor, Cyber Towers, HITEC City', 'Hyderabad', 'Telangana', '500081', 'India', true),
(1001, 'HOME', '89, Civil Lines, Mall Road', 'Indore', 'Madhya Pradesh', '452001', 'India', true);

-- Sample data for fc_customer.customer_document table (India Region)
INSERT INTO fc_customer.customer_document (customer_id, document_type, document_number, file_path, verification_status, uploaded_at) VALUES
(1002, 'PASSPORT', 'M1234567', '/documents/customer/1002/passport_m1234567.pdf', 'VERIFIED', '2024-01-15 11:00:00'),
(1003, 'DRIVING_LICENSE', 'DL1320110012345', '/documents/customer/1003/license_dl1320110012345.pdf', 'VERIFIED', '2024-01-16 12:15:00'),
(1004, 'PASSPORT', 'K9876543', '/documents/customer/1004/passport_k9876543.pdf', 'VERIFIED', '2024-01-17 09:45:00'),
(1005, 'DRIVING_LICENSE', 'TN0320090056789', '/documents/customer/1005/license_tn0320090056789.pdf', 'VERIFIED', '2024-01-18 14:45:00'),
(1006, 'PASSPORT', 'J5678901', '/documents/customer/1006/passport_j5678901.pdf', 'VERIFIED', '2024-01-19 17:00:00'),
(1007, 'DRIVING_LICENSE', 'MH0220080034567', '/documents/customer/1007/license_mh0220080034567.pdf', 'PENDING', '2024-01-20 09:15:00'),
(1008, 'PASSPORT', 'H3456789', '/documents/customer/1008/passport_h3456789.pdf', 'VERIFIED', '2024-01-21 13:45:00'),
(1009, 'DRIVING_LICENSE', 'KL0720110098765', '/documents/customer/1009/license_kl0720110098765.pdf', 'VERIFIED', '2024-01-22 10:30:00'),
(1001, 'PASSPORT', 'G2345678', '/documents/customer/1001/passport_g2345678.pdf', 'VERIFIED', '2024-01-24 12:30:00');