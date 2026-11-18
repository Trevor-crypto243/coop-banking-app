# Co-op Banking App

A small Spring Boot service with three APIs:

1. **Onboard a customer** – takes the customer's details, saves them and opens an account for them.
2. **Funds transfer** – moves money from one customer's account to another and updates both balances.
3. **Balance enquiry** – returns a customer's current balance in a friendly format.

All requests and responses are JSON. Inputs are validated, every request is logged with a request id, and errors always come back in the same shape.

## Tech stack

- Java 21
- Spring Boot 3.3 (Web, Data JPA, Validation)
- H2 database (file based, so data survives a restart)
- JUnit 5 + MockMvc for tests

## Running it

You only need Java 21. The Maven wrapper is included.

```bash
git clone https://github.com/Trevor-crypto243/coop-banking-app.git
cd coop-banking-app
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

Run the tests with:

```bash
./mvnw test
```

To look at the database, open `http://localhost:8080/h2-console` and use JDBC URL `jdbc:h2:file:./data/bankdb`, user `sa`, no password.

## Project structure

```
src/main/java/ke/coop/bank
├── BankApplication.java
├── config/        RequestLoggingFilter – adds a request id to every log line
├── controller/    REST endpoints, kept thin
├── service/       business logic (onboarding, transfer, balance)
├── repository/    Spring Data JPA repositories
├── model/         JPA entities: Customer, Account, Transaction
├── dto/           request / response records + the ApiResponse wrapper
└── exception/     custom exceptions + GlobalExceptionHandler
```

## API

Every response uses the same envelope:

```json
{
  "status": "SUCCESS | FAILED",
  "message": "human readable message",
  "data": { },
  "timestamp": "2026-11-18T11:24:22"
}
```

### 1. Create customer

`POST /api/v1/customers`

```json
{
  "firstName": "Jane",
  "lastName": "Wanjiku",
  "nationalId": "12345678",
  "email": "jane@example.com",
  "phoneNumber": "0712345678",
  "openingBalance": 15000
}
```

Response `201 Created`:

```json
{
  "status": "SUCCESS",
  "message": "Welcome Jane! Your account 0149895421023 is ready.",
  "data": {
    "customerId": 1,
    "fullName": "Jane Wanjiku",
    "email": "jane@example.com",
    "accountNumber": "0149895421023",
    "balance": 15000,
    "currency": "KES"
  },
  "timestamp": "2026-11-18T11:24:21"
}
```

Validation rules: names and email are required, national ID is 7–8 digits, phone must be a Kenyan number (`07..`, `01..` or `+254..`), opening balance can't be negative. National ID and email must be unique.

### 2. Transfer funds

`POST /api/v1/transfers`

```json
{
  "fromAccount": "0149895421023",
  "toAccount": "0194624317827",
  "amount": 2500.50,
  "narration": "Rent"
}
```

Response `200 OK`:

```json
{
  "status": "SUCCESS",
  "message": "KES 2,500.50 sent to 0194624317827. Ref: FT26111811240A004A",
  "data": {
    "reference": "FT26111811240A004A",
    "fromAccount": "0149895421023",
    "toAccount": "0194624317827",
    "amount": 2500.50,
    "fromAccountBalance": 12499.50,
    "toAccountBalance": 3000.50,
    "transactionDate": "2026-11-18T11:24:22"
  },
  "timestamp": "2026-11-18T11:24:22"
}
```

How the transfer works:

- The debit, the credit and the transaction record all run in **one database transaction**. If any step fails, the whole thing rolls back and no money is lost or created.
- Both account rows are locked (`SELECT ... FOR UPDATE`) before the balances are read. Two transfers on the same account can't both read the old balance and overdraw it.
- The accounts are always locked in the same order (smaller account number first). That way a transfer A → B and a transfer B → A running at the same time can't deadlock.
- Each transfer gets a reference like `FT2611181124XXXXXX` and is saved in the `transactions` table.

### 3. Balance enquiry

`GET /api/v1/accounts/{accountNumber}/balance`

Response `200 OK`:

```json
{
  "status": "SUCCESS",
  "message": "Dear Jane Wanjiku, your account balance is KES 12,499.50",
  "data": {
    "accountNumber": "0149895421023",
    "accountName": "Jane Wanjiku",
    "balance": 12499.50,
    "currency": "KES",
    "formattedBalance": "KES 12,499.50",
    "asAt": "2026-11-18T11:24:22"
  },
  "timestamp": "2026-11-18T11:24:22"
}
```

## Error handling

| Case | HTTP status | Example message |
|---|---|---|
| Missing / invalid fields | 400 | `Validation failed` (field errors listed in `data`) |
| Body isn't valid JSON | 400 | `Request body is missing or is not valid JSON` |
| Same source and destination | 400 | `Source and destination account cannot be the same` |
| Duplicate national ID / email | 400 | `A customer with this national ID already exists` |
| Account doesn't exist | 404 | `Account 0100000000000 does not exist` |
| Not enough money | 422 | `Insufficient funds in account ... Available: 3,000.50, requested: 999,999.00` |
| Anything unexpected | 500 | generic message, the stack trace is only logged |

Validation error example:

```json
{
  "status": "FAILED",
  "message": "Validation failed",
  "data": {
    "fromAccount": "source account is required",
    "toAccount": "destination account is required",
    "amount": "minimum transfer amount is KES 1.00"
  },
  "timestamp": "2026-11-18T11:24:22"
}
```

## Logging

Each request gets a short id. The id goes into every log line for that request and into the `X-Request-Id` response header, so a single call can be followed through the logs:

```
11:24:21.451 INFO  [4ef487f4] k.c.b.s.CustomerService - Onboarding new customer, nationalId=****678
11:24:21.612 INFO  [4ef487f4] k.c.b.s.CustomerService - Customer 1 created with account 0149895421023
11:24:21.640 INFO  [4ef487f4] k.c.b.c.RequestLoggingFilter - POST /api/v1/customers -> 201 (198 ms)
```

National IDs are masked in the logs.

## Trying it out

`requests.http` has ready-made requests. It runs straight from IntelliJ or the VS Code REST Client extension. With curl:

```bash
curl -X POST localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Wanjiku","nationalId":"12345678","email":"jane@example.com","phoneNumber":"0712345678","openingBalance":15000}'
```

## What I'd add next

- Authentication (JWT / OAuth2), so customers can only see their own accounts
- An idempotency key on transfers, so a retried request isn't processed twice
- Swap H2 for PostgreSQL and manage the schema with Flyway
- Transaction history endpoint
