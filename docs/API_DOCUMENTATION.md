# CIF Bank REST API Documentation

## Overview

The CIF Bank service registers a customer (CIF), assigns a TPIN, authenticates a CIF/TPIN combination, blocks a CIF after three consecutive failed TPIN attempts, and exposes a reset operation.

- Base URL: `http://localhost:8080`
- Content type: `application/json`
- CIF identifier: UUID returned by the Register API
- Storage: H2 database
- Log file: `logs/cifbank.log`
- Machine-readable contract: [`openapi.yaml`](openapi.yaml)

> Security note: the current implementation accepts TPIN values in URL path parameters. TPINs are redacted in application log messages, but URLs may still be recorded by infrastructure. A production API should accept TPIN values only in request bodies over HTTPS.

## Running the application

Requirements:

- Java 17

From the project root on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Or run the packaged application:

```powershell
java -jar target\cifbank-0.0.1-SNAPSHOT.jar
```

## Authentication

The Register, Set TPIN, and Authenticate endpoints are public in the current security configuration. Authentication is performed using the CIF UUID and TPIN. Reset TPIN still requires HTTP Basic authentication using the registered `email` and `password`.

The HTTP Basic password is separate from the TPIN.

## Recommended workflow

1. Register a customer and copy the returned `uuid`.
2. Set a TPIN using that UUID.
3. Authenticate using the UUID and TPIN.
4. Three consecutive incorrect TPIN attempts set the CIF's failed-attempt count to three.
5. The third failed attempt immediately returns `423 Locked`; later attempts remain blocked.
6. Reset TPIN should unblock the CIF and assign a new TPIN. See the known limitation below.

## Endpoints

### 1. Register CIF

`POST /user`

Authentication: none.

Request:

```json
{
  "firstName": "Noha",
  "lName": "Nabawy",
  "iban": "EG380019000500000000263180002",
  "accountNumber": "ACC-10001",
  "phoneNumber": "+201001234567",
  "berthofDate": "1990-01-15",
  "email": "noha@example.com",
  "password": "Bank@123"
}
```

Successful response — `201 Created`:

```json
{
  "message": "Customer registered successfully",
  "uuid": "12d41523-3a51-420b-8c4a-674dba6e36f3"
}
```

The service validates required fields and formats, normalizes the email and IBAN, encodes the password, and assigns `ROLE_USER` internally. Email, account number, and IBAN must each be unique.

Invalid request — `400 Bad Request`:

```json
{
  "status": 400,
  "message": "Request validation failed",
  "path": "/user",
  "timestamp": "2026-10-23T10:00:00",
  "errors": {
    "email": "Email must be valid",
    "password": "Password must contain between 8 and 72 characters"
  }
}
```

Duplicate customer — `409 Conflict`:

```json
{
  "status": 409,
  "message": "A customer with this email already exists",
  "path": "/user",
  "timestamp": "2026-10-23T10:00:00"
}
```

### 2. Set TPIN

`PUT /user/{id}/{tpin}`

Authentication: none.

Example:

```http
PUT /user/12d41523-3a51-420b-8c4a-674dba6e36f3/1234
```

Successful response — `200 OK`:

```json
{
  "message": "Customer Update  successfully",
  "uuid": "12d41523-3a51-420b-8c4a-674dba6e36f3"
}
```

Customer not found — `404 Not Found`:

```json
{
  "status": 404,
  "message": "Customer Id Not found:Customer ID not found: 12d41523-3a51-420b-8c4a-674dba6e36f3",
  "path": "/user/12d41523-3a51-420b-8c4a-674dba6e36f3/1234",
  "timestamp": "2026-10-23T10:00:00"
}
```

The TPIN is encoded before storage.

### 3. Authenticate CIF and TPIN

`POST /auth/{customerId}/{tpin}`

Authentication: none. The CIF UUID and TPIN are the credentials being verified.

Example:

```http
POST /auth/12d41523-3a51-420b-8c4a-674dba6e36f3/1234
```

Responses are plain text:

| Situation | HTTP status | Response body |
|---|---:|---|
| Correct TPIN | `200 OK` | `Authentication successful` |
| First or second incorrect TPIN | `401 Unauthorized` | `Invalid CIF or TPIN` |
| Third incorrect TPIN | `423 Locked` | `CIF is blocked` |
| CIF already blocked | `423 Locked` | `CIF is blocked` |

A successful authentication resets the failed-attempt counter to zero. The third consecutive failure sets the counter to three and reports the blocked state immediately.

### 4. Reset TPIN

`PUT /auth/{customerId}/{tpin}`

Authentication: HTTP Basic using the registered email/password.

The endpoint currently requires a request body:

```json
{
  "tPin": 5678
}
```

Current successful response — `200 OK`:

```text
Tpin Update Sucess
```

#### Known implementation limitation

The current controller ignores `tPin` from the request body. It sends the path TPIN to the service, and the service only succeeds when that TPIN matches the existing TPIN. It then re-encodes the same value and resets failed attempts. Consequently, this operation can unblock a CIF but **cannot assign a different new TPIN**. This must be corrected before claiming full compliance with the interview requirement.

If the path TPIN does not match the stored TPIN, the current implementation returns `200 OK` with an empty body.

## Logging

Controller request and successful response messages are written to `logs/cifbank.log`. Passwords and TPIN values are redacted from those explicit messages. Errors handled by the custom customer-not-found handler are also logged.

The current implementation does not provide a global request/response logging filter, so requests rejected before controller execution and unhandled failures are not guaranteed to have complete request/response log entries.

## Postman

Import this file from the `postman` directory:

- `CIF-Bank.postman_collection.json`

No Postman environment is required. Start the application on `http://localhost:8080` and run the collection in its numbered order. Registration generates a unique email and stores the returned CIF UUID inside the collection automatically so later requests can use it.

