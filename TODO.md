## Auth Server Architecture

### **Once completed, move all JSONB field to POJO class**

### Auth Server Registration Strategy: **Hybrid Model**

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Eureka Server │    │   Auth Server    │    │  Gateway Server │
│     (8761)      │◄───┤     (8090)       │───►│     (8080)      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │                          │
                              ▼                          ▼
                       ┌─────────────┐            ┌─────────────┐
                       │   Client    │            │   Service   │
                       │Credentials  │            │ Validation  │
                       └─────────────┘            └─────────────┘
```

### Authentication Approach:
- **Auth Server**: Registers with Eureka for service discovery and load balancing
- **External API Calls**: `Client → Gateway → Token Validation → Business Service`
- **Inter-Service Calls**: Services use client credentials for authentication
- **Token Types**: Different tokens for users vs service-to-service communication

## Entity Models

### Customer Service Entities

```
Customer:
- customer_id (BIGSERIAL, Primary Key)
- customer_number (String, Unique)
- first_name (String)
- last_name (String)
- email (String, Unique)
- phone_number (String)
- date_of_birth (Date)
- customer_type (Enum: INDIVIDUAL, BUSINESS)
- kyc_status (Enum: PENDING, VERIFIED, REJECTED)
- risk_profile (Enum: LOW, MEDIUM, HIGH)
- created_at (Timestamp)
- updated_at (Timestamp)
- is_active (Boolean)

Address:
- address_id (BIGSERIAL, Primary Key)
- customer_id (BIGSERIAL, Foreign Key)
- address_type (Enum: HOME, WORK, MAILING)
- street_address (String)
- city (String)
- state (String)
- zip_code (String)
- country (String)
- is_primary (Boolean)

Customer_Documents:
- document_id (BIGSERIAL, Primary Key)
- customer_id (BIGSERIAL, Foreign Key)
- document_type (Enum: PASSPORT, DRIVING_LICENSE, UTILITY_BILL, AADHAR_CARD)
- document_number (String)
- file_path (String)
- verification_status (Enum: PENDING, VERIFIED, REJECTED)
- uploaded_at (Timestamp)
```

### Account Service Entities

```
Account:
- account_id (BIGSERIAL, Primary Key)
- account_number (String, Unique)
- customer_id (BIGSERIAL, Soft Reference to Customer Service)
- product_id (BIGSERIAL, Foreign Key to Account_Product)
- account_status (Enum: ACTIVE, INACTIVE, CLOSED, SUSPENDED)
- balance (Decimal)
- available_balance (Decimal)
- currency_code (String)
- custom_interest_rate (Decimal, nullable) - overrides product if set
- custom_minimum_balance (Decimal, nullable) - overrides product if set
- custom_overdraft_limit (Decimal, nullable) - overrides product if set
- opened_at (Timestamp)
- closed_at (Timestamp)
- last_activity_date (Date)

Account_Product:
- product_id (BIGSERIAL, Primary Key)
- product_name (String)
- product_type (Enum: CHECKING, SAVINGS, CREDIT, LOAN)
- interest_rate (Decimal)
- minimum_balance (Decimal)
- overdraft_limit (Decimal)
- monthly_fee (Decimal)
- features (JSON)
- is_active (Boolean)
- created_at (Timestamp)
- updated_at (Timestamp)

Account_Beneficiary:
- beneficiary_id (BIGSERIAL, Primary Key)
- account_id (BIGSERIAL, Foreign Key)
- beneficiary_name (String)
- relationship (String)
- percentage (Decimal)
- contact_info (JSON)
```

### Transaction Service Entities
```
Transaction_Subcategory:
- subcategory_id (BIGSERIAL, Primary Key)
- subcategory_name (String)
- parent_category (Enum: FOOD_DINING, TRANSPORTATION, SHOPPING, BILLS_UTILITIES, ENTERTAINMENT, HEALTHCARE, INCOME, TRANSFER, PERSONAL_CARE, EDUCATION, TRAVEL, INVESTMENTS)
- is_active (Boolean)
- created_at (Timestamp)

Transaction:
- transaction_id (BIGSERIAL, Primary Key)
- transaction_reference (String, Unique)
- from_account_id (BIGSERIAL, Soft Reference to Account Service)
- to_account_id (BIGSERIAL, Soft Reference to Account Service)
- transaction_type (Enum: DEBIT, CREDIT, TRANSFER)
- amount (Decimal)
- currency_code (String)
- description (String)
- subcategory_id (BIGSERIAL, Foreign Key to Transaction_Subcategory)
- transaction_status (Enum: PENDING, COMPLETED, FAILED, CANCELLED)
- transaction_date (Timestamp)
- value_date (Date)
- channel (Enum: ATM, ONLINE, MOBILE, BRANCH, UPI)
- reference_number (String)
- merchant_info (JSON)
- location (JSON)
- created_at (Timestamp)
- updated_at (Timestamp)

Transaction_Limits:
- limit_id (BIGSERIAL, Primary Key)
- account_id (BIGSERIAL, Soft Reference to Account Service)
- limit_type (Enum: DAILY, WEEKLY, MONTHLY, TRANSACTION)
- limit_amount (Decimal)
- used_amount (Decimal)
- reset_frequency (String)
- last_reset_date (Date)
- created_at (Timestamp)
- updated_at (Timestamp)
```

### User Service Entities
```
User:
- user_id (UUID, Primary Key)
- username (String, Unique)
- email (String, Unique)
- password_hash (String) - encrypted
- user_type (Enum: CUSTOMER, EMPLOYEE, ADMIN, PARTNER)
- account_status (Enum: ACTIVE, INACTIVE, LOCKED, PENDING_VERIFICATION)
- failed_login_attempts (Integer)
- last_login_at (Timestamp)
- password_changed_at (Timestamp)
- created_at (Timestamp)
- updated_at (Timestamp)
- is_active (Boolean)

User_Role:
- role_id (UUID, Primary Key)
- role_name (String) - "CUSTOMER", "PREMIUM_CUSTOMER", "BUSINESS_USER", "ADMIN", "SUPPORT"
- permissions (JSON) - list of permissions
- is_active (Boolean)

User_Role_Mapping:
- user_id (UUID, Foreign Key to User)
- role_id (UUID, Foreign Key to User_Role)
- assigned_at (Timestamp)
- assigned_by (UUID)

Customer_User_Mapping:
- mapping_id (UUID, Primary Key)
- customer_id (UUID, Soft Reference to Customer Service)
- user_id (UUID, Foreign Key to User)
- relationship_type (Enum: PRIMARY, SECONDARY, AUTHORIZED_USER)
- permissions (JSON) - account-specific permissions
- is_active (Boolean)
- created_at (Timestamp)
```

## API Endpoints

### Customer Service Endpoints
```
GET /api/v1/customers
- Description: Retrieve paginated list of customers with filtering
- Query Params: page, size, status, customerType, riskProfile

GET /api/v1/customers/{customerId}
- Description: Get detailed customer information including addresses and documents

POST /api/v1/customers
- Description: Create new customer profile with KYC initiation

PUT /api/v1/customers/{customerId}
- Description: Update customer information and trigger re-verification if needed

GET /api/v1/customers/{customerId}/accounts
- Description: Get all accounts associated with a customer (calls Account Service)

GET /api/v1/customers/search
- Description: Advanced customer search with complex criteria
- Query Params: email, phone, accountNumber

POST /api/v1/customers/{customerId}/documents
- Description: Upload customer documents for verification

GET /api/v1/customers/kyc/pending
- Description: Get customers pending KYC verification

PUT /api/v1/customers/{customerId}/kyc-status
- Description: Update KYC verification status

POST /api/v1/customers/{customerId}/validate
- Description: Validate customer exists (for inter-service calls)
```

### Account Service Endpoints

```
GET /api/v1/accounts - completed
- Description: Get paginated accounts with filtering by status, type, balance range, created date

GET /api/v1/accounts/{accountId}
- Description: Get detailed account information including recent transactions

POST /api/v1/accounts
- Description: Create new account (validates customer via Customer Service)

PUT /api/v1/accounts/{accountNumber}/status - completed
- Description: Update account status (activate, suspend, close)

GET /api/v1/accounts/{accountNumber}/balance - completed
- Description: Get real-time account balance and available funds

GET /api/v1/accounts/{accountId}/statements
- Description: Generate and retrieve account statements
- Query Params: fromDate, toDate, format (PDF/CSV)

POST /api/v1/accounts/{accountNumber}/freeze - completed
- Description: Temporarily freeze account for security reasons

GET /api/v1/accounts/customer/{customerId} - completed
- Description: Get all accounts for a specific customer
- can be executed via /api/v1/accounts using Query Param

GET /api/v1/accounts/dormant
- Description: Get accounts that are dormant based on inactivity

PUT /api/v1/accounts/{accountId}/limits
- Description: Update transaction limits for account

POST /api/v1/accounts/{accountId}/validate
- Description: Validate account exists (for inter-service calls)
```

### Transaction Service Endpoints

```
GET /api/v1/transactions - Completed
- Description: Get paginated transactions with complex filtering
- Query Params: accountId, fromDate, toDate, amount, type, status, channel

GET /api/v1/transactions/{transactionId} - Completed
- Description: Get detailed transaction information with full audit trail

POST /api/v1/transactions/transfer
- Description: Process fund transfer (validates accounts via Account Service)

POST /api/v1/transactions/deposit
- Description: Process deposit transaction

POST /api/v1/transactions/withdrawal
- Description: Process withdrawal transaction

GET /api/v1/transactions/account/{accountId} 
- Description: Get transaction history for specific account
- can be executed with /api/v1/transactions using query params

GET /api/v1/transactions/analytics/spending
- Description: Get spending analytics and categorization
- Query Params: accountId, period, categoryId

GET /api/v1/transactions/search 
- Description: Advanced transaction search with complex criteria
- can be executed with /api/v1/transactions using query params
- More criteria to be added later (minAmount, maxAmount, description, reference number, subcategory, parentCategory)

POST /api/v1/transactions/{transactionId}/dispute
- Description: Initiate dispute process for transaction

GET /api/v1/transactions/bulk-status
- Description: Get status of bulk transaction processing

POST /api/v1/transactions/recurring
- Description: Set up recurring transaction schedule

GET /api/v1/transactions/suspicious
- Description: Get transactions flagged for fraud review
```

## Additional Enterprise Features

### 1. **Monitoring & Observability**
- Spring Boot Actuator for health checks and metrics
- Micrometer for application metrics collection
- Distributed tracing with correlation IDs
- Centralized logging with structured format

### 2. **Security Features**
- Data encryption at-rest and in-transit
- OAuth2/JWT for API security
- Complete audit logging for compliance
- Input validation and sanitization
- GDPR compliance features

### 3. **Resilience Patterns**
- Circuit breaker for fault tolerance
- Retry logic with exponential backoff
- Bulkhead pattern for resource isolation
- Request timeout management

### 4. **Performance Optimization**
- Redis distributed caching
- Database connection pooling (HikariCP)
- Optimized database indexes
- Async processing for non-blocking operations

### 5. **Data Management**
- Read replicas for query optimization
- Automated data archival strategies
- Backup and disaster recovery
- Database migration tools

## Complex Query Requirements

### Customer Service Queries
- Customer segmentation based on multiple criteria
- KYC compliance and risk assessment reporting
- Customer lifetime value calculations
- Dormant customer identification and re-engagement

### Account Service Queries
- Multi-dimensional account balance aggregations
- Interest calculations across account types
- Regulatory compliance reporting
- Account performance analytics and trend analysis

### Transaction Service Queries
- Real-time fraud detection pattern analysis
- Transaction volume and velocity monitoring
- Advanced spending categorization and analysis
- Settlement and reconciliation reporting
- Balance calculations with pending transactions

## System Architecture Flow

```
                    Internet Request
                          ↓
                    Load Balancer
                          ↓
                    Gateway Server (8080) ← Auth Server (8090)
                          ↓                      ↑
                    Eureka Server (8761) ← Config Server (8888)
                          ↓
        ┌─────────────────┼─────────────────┐
        ↓                 ↓                 ↓
Customer Service  Account Service   Transaction Service
    (8081)           (8082)             (8083)
      ↓                ↓                  ↓
PostgreSQL       PostgreSQL         PostgreSQL
(customer_db)    (account_db)      (transaction_db)
```