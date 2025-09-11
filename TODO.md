## Auth Server Architecture

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
- Query Params: email, phone, ssn, accountNumber

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
GET /api/v1/accounts
- Description: Get paginated accounts with filtering by status, type, balance range

GET /api/v1/accounts/{accountId}
- Description: Get detailed account information including recent transactions

POST /api/v1/accounts
- Description: Create new account (validates customer via Customer Service)

PUT /api/v1/accounts/{accountId}/status
- Description: Update account status (activate, suspend, close)

GET /api/v1/accounts/{accountId}/balance
- Description: Get real-time account balance and available funds

GET /api/v1/accounts/{accountId}/statements
- Description: Generate and retrieve account statements
- Query Params: fromDate, toDate, format (PDF/CSV)

POST /api/v1/accounts/{accountId}/freeze
- Description: Temporarily freeze account for security reasons

GET /api/v1/accounts/customer/{customerId}
- Description: Get all accounts for a specific customer

GET /api/v1/accounts/dormant
- Description: Get accounts that are dormant based on inactivity

PUT /api/v1/accounts/{accountId}/limits
- Description: Update transaction limits for account

POST /api/v1/accounts/{accountId}/validate
- Description: Validate account exists (for inter-service calls)
```

### Transaction Service Endpoints

```
GET /api/v1/transactions
- Description: Get paginated transactions with complex filtering
- Query Params: accountId, fromDate, toDate, amount, type, status, channel

GET /api/v1/transactions/{transactionId}
- Description: Get detailed transaction information with full audit trail

POST /api/v1/transactions/transfer
- Description: Process fund transfer (validates accounts via Account Service)

POST /api/v1/transactions/deposit
- Description: Process deposit transaction

POST /api/v1/transactions/withdrawal
- Description: Process withdrawal transaction

GET /api/v1/transactions/account/{accountId}
- Description: Get transaction history for specific account

GET /api/v1/transactions/analytics/spending
- Description: Get spending analytics and categorization
- Query Params: accountId, period, categoryId

GET /api/v1/transactions/search
- Description: Advanced transaction search with complex criteria

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