# Welcome to Kasi Bank

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Concepts](#concepts)

## Overview

This project is a demo project for creating a banking API.\
The project aims to make use of best practices within Java Spring Boot and API development in general.

## Project Structure

```
kasi_bank(project-root)/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── demo/
│                   └── kasi_bank/
│                       ├── config/
│                       ├── controller/
│                       ├── dto/
│                       ├── entity/
│                       ├── enums/
│                       ├── exception/
│                       ├── repository/
│                       ├── service/
│                       └── utils/
├── .gitignore
├── pom.xml
└── README.md
```

## Concepts

### Currently Implemented

#### Security

- JWT Authentication

#### Input Validation

- Input validation to prevent SQL injection and other vulnerabilities.
- Sanitized user input before using it in database queries.
- Implemented a global exception handler to handle validation errors and communicate clearly with client side.

#### Data Encryption

- Encrypted user passwords at rest (in the database) and in transit (during network communication).

#### Performance:

- Implemented basic caching to store account enquiry data and regularly evict the cache on updates.

#### Reliability:

- Implemented error handling for common situations.
- Added additional error handling for unexpected circumstances.
- Added basic logging for debugging.

### Next Steps

#### Scale

**Database Choice**:

- This project makes use of MySQL which is not horizontally scalable by default.
- To improve scalability going forward the database will be migrated to PostgresSQL or MySQL Cluster.
- This allows for more database servers to handle increased load.

**Database Connection Pooling**:

- Implement connection pooling to efficiently manage database connections. 
- This prevents creating a new connection for
  every request, improving performance under high load.

**Microservices Architecture**:

- As the API grows in functionality break it down further into microservices.

#### Security:

- Implement control access to API endpoints based on user roles and permissions.
- Add further data encryption to other sensitive data such as account numbers and balances.

#### Performance:

- Profile the API and optimize code for performance.

#### Reliability:

- Implement more robust error handling to gracefully handle unexpected situations.
- Implement a circuit breaker pattern to handle failing external dependencies gracefully.
- Set up monitoring tools to track API health and performance metrics.