# Banking App (Spring Boot + Angular)

A small core-banking app: staff sign in, open accounts, deposit, withdraw, transfer between accounts and view transaction history.

- **Backend:** Java 21, Spring Boot 3.3, Spring Security (stateless JWT, BCrypt), Spring Data JPA, Bean Validation, H2 (dev) / PostgreSQL, OpenAPI/Swagger.
- **Frontend:** Angular 21 (standalone components, signals, OnPush, zoneless), reactive forms, functional HTTP interceptor and route guards, lazy-loaded routes.

## Run it

### Option 1: local dev (no Docker, in-memory H2)

```bash
# terminal 1: API on :8080
cd backend
./mvnw spring-boot:run

# terminal 2: UI on :4200 (proxies /api to :8080)
cd frontend
npm install
npm start
```

Open http://localhost:4200 and sign in with **admin / Admin@123**.

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:bank`, user `sa`, no password)

### Option 2: everything in Docker (PostgreSQL)

```bash
docker compose up -d --build
```

UI at http://localhost:4200, API at http://localhost:8080. Data persists in the `pgdata` volume.

### Tests

```bash
cd backend && ./mvnw test        # 10 tests: service rules, rollback, concurrency, API + security
cd frontend && npx ng build      # production build (type-checks templates)
```

## API

All `/api/v1/**` endpoints need `Authorization: Bearer <token>`.

| Method | Path | Body | Success | Errors |
|---|---|---|---|---|
| POST | `/api/auth/login` | `{username, password}` | 200 `{token, username, role, expiresInSeconds}` | 400, 401 |
| GET | `/api/auth/me` | | 200 | 401 |
| GET | `/api/v1/accounts?page=0&size=20` | | 200 page of accounts | 401 |
| GET | `/api/v1/accounts/{id}` | | 200 | 404 |
| POST | `/api/v1/accounts` | `{holderName, openingBalance?}` | 201 + `Location` | 400 |
| POST | `/api/v1/accounts/{id}/deposit` | `{amount, narration?}` | 200 updated account | 400, 404 |
| POST | `/api/v1/accounts/{id}/withdraw` | `{amount, narration?}` | 200 updated account | 400, 404, 422 insufficient funds |
| GET | `/api/v1/accounts/{id}/transactions?page=0&size=10` | | 200 page, newest first | 404 |
| POST | `/api/v1/transfers` | `{fromAccountId, toAccountId, amount, narration?}` | 200 `{reference, from, to}` | 400, 404, 422 |

Errors are RFC 7807 `ProblemDetail` JSON, e.g.

```json
{"title":"Unprocessable Entity","status":422,"detail":"Insufficient funds in account 0182727621009"}
```

Validation errors add an `errors` map: `{"errors": {"amount": "must be greater than 0"}}`.

Quick check with curl:

```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"Admin@123"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')
curl -s localhost:8080/api/v1/accounts -H "Authorization: Bearer $TOKEN"
curl -s -X POST localhost:8080/api/v1/transfers -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"fromAccountId":1,"toAccountId":2,"amount":500}'
```

## Design decisions

**Money and ACID**
- Amounts are `BigDecimal` with `NUMERIC(19,2)`, never `double`.
- `transfer()` is one `@Transactional` method: debit, credit and both ledger rows commit together or not at all. Any exception (e.g. insufficient funds) rolls everything back; a test proves it.
- Rows are locked with `SELECT ... FOR UPDATE` (`PESSIMISTIC_WRITE`) **in ascending id order**, so A→B and B→A running at the same time cannot deadlock. A test fires 100 concurrent opposite transfers and checks the total is unchanged.
- `@Version` on `Account` adds optimistic locking as a second guard; a conflict returns 409.
- Every movement writes a `Transaction` row with `balanceAfter`; both legs of a transfer share one `reference`.

**Security**
- Passwords hashed with BCrypt. Login returns the same message for unknown user and wrong password (no username enumeration).
- Stateless JWT (HS384, issuer checked, 60-minute expiry). The secret comes from `APP_JWT_SECRET`; startup fails if it is shorter than 32 characters.
- CSRF disabled because no cookies are used; CORS limited to the configured origins.
- The UI stores the token in `sessionStorage` (cleared when the tab closes); the interceptor logs out on any 401.

**Structure (SOLID)**
- Controller (HTTP) → Service (business rules, transactions) → Repository (data). Entities never leave the service layer; records are used as DTOs.
- Constructor injection everywhere. One `@RestControllerAdvice` maps exceptions to status codes.

**Performance**
- `open-in-view: false`, read-only transactions for queries, lazy `ManyToOne`, JDBC batching, composite index on `(account_id, createdAt)` for history, a unique index on `accountNumber`, server-side pagination with a max page size of 100, gzip compression.
- Angular: lazy routes (login page loads ~1 kB of its own code), OnPush + signals, `@for` with `track`, nginx caches hashed bundles for a year and never caches `index.html`.

## Project layout

```
backend/src/main/java/ke/coop/bank/
  config/       SecurityConfig, DataSeeder
  domain/       Account, Transaction, AppUser (JPA entities)
  repository/   Spring Data repositories (incl. FOR UPDATE lookup)
  security/     JwtService, JwtAuthFilter, JwtProperties
  service/      AccountService, AuthService, domain exceptions
  web/          AccountController, AuthController, GlobalExceptionHandler, dto/
frontend/src/app/
  core/         AuthService, authInterceptor, guards, BankApiService, models
  pages/        login, accounts (list), open-account, transfer, account-detail (deposit/withdraw + history)
  app.*         shell: top bar + sidebar with collapsible submenus (Accounts > All / Open, Payments > Transfer);
                becomes a hamburger drawer under 800px
```

## Configuration

| Env var | Default | Purpose |
|---|---|---|
| `APP_JWT_SECRET` | dev-only value | JWT signing key (32+ chars) |
| `APP_ADMIN_PASSWORD` | `Admin@123` | Seeded admin password |
| `APP_CORS_ORIGINS` | `http://localhost:4200` | Comma-separated allowed origins |
| `SPRING_PROFILES_ACTIVE` | (none = H2) | `postgres` to use PostgreSQL |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | `localhost:5432/bank`, `bank`, `bank` | PostgreSQL connection |
