# Co-op Banking App

A simple Spring Boot banking API with 3 endpoints:

- Create a customer (saves the customer details and opens an account)
- Transfer funds from one account to another
- Check an account balance

## Tech

Java 21, Spring Boot 3, Spring Data JPA, H2, Lombok, Swagger

## How to run it

```bash
git clone https://github.com/Trevor-crypto243/coop-banking-app.git
cd coop-banking-app
./mvnw spring-boot:run
```

The app will starts on `http://localhost:8080`.

- Swagger UI: http://localhost:8080/swagger-ui.html
- API docs (json): http://localhost:8080/api-docs
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/bankdb`, user `sa`, no password)

Testing:

```bash
./mvnw test
```

## Endpoints

| Method | URL | Description |
|---|---|---|
| POST | `/api/v1/customers` | create customer + account |
| POST | `/api/v1/transfers` | transfer funds |
| GET | `/api/v1/accounts/{accountNumber}/balance` | get account balance |

### Create customer

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

### Transfer

```json
{
  "fromAccount": "0149895421023",
  "toAccount": "0194624317827",
  "amount": 2500,
  "narration": "Rent"
}
```

### Balance response

```json
{
  "status": "SUCCESS",
  "message": "Dear Jane Wanjiku, your account balance is KES 12,500.00",
  "data": {
    "accountNumber": "0149895421023",
    "accountName": "Jane Wanjiku",
    "balance": 12500.00,
    "currency": "KES",
    "formattedBalance": "KES 12,500.00",
    "asAt": "2026-11-18T11:24:22"
  },
  "timestamp": "2026-11-18T11:24:22"
}
```

Errors come back in the same format with `"status": "FAILED"` and a message (400 for validation errors, 404 if the account doesn't exist, 422 for insufficient funds).

There's also a `requests.http` file with sample requests.

## TODO

- Add authentication
- Lock the accounts during a transfer to handle concurrent transfers
- Use PostgreSQL instead of H2
