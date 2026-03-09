# Identity & Authentication Prototype

Backend architecture experiment exploring scalable identity systems using Java, Domain Driven Design, and Clean Architecture.

This repository contains an experimental backend implementation inspired by large-scale platform identity systems.

The focus of this project is to explore how a large-scale identity system could be designed using **Domain Driven Design (DDD)** and **Clean Architecture** principles.

The implementation currently focuses on **identity creation, OTP verification, digital identity generation, and stateless login concepts**.

---

## Status

🚧 Experimental prototype  
Current focus: **Identity and Authentication subsystem**

---

## Overview

The project implements the early foundation required for a **Sign-Up and Identity Creation flow** commonly found in large platforms.

Typical steps include:

1. Sign up using email and phone
2. Authenticate the identity
3. Receive a **unique digital identity**
4. Be able to sign in securely

This repository explores how such a system could be implemented in a backend service.

---

## Architecture Approach

The project follows **Domain Driven Design and Clean Architecture principles**.

The design focuses on:

- Domain-first modeling
- Aggregate-based state management
- Explicit business invariants
- Separation between domain and infrastructure layers

The Identity subsystem is modeled around a **DigitalIdentity aggregate**, which owns the lifecycle of identity creation and verification.

---

## Architecture Goals

The project explores several backend architecture concepts commonly required in large-scale platforms:

- Designing identity systems with strict domain invariants
- Generating globally unique and time-ordered digital identifiers
- Separating domain logic from infrastructure frameworks
- Modeling identity lifecycle and verification workflows
- Supporting stateless authentication patterns

---

## Identity Subsystem

The Identity subsystem handles:

- User identity creation
- OTP-based verification
- Digital identity generation
- Identity state transitions

The domain model enforces important rules such as:

- Digital ID can only be generated **after OTP verification**
- Identity state transitions are controlled inside the aggregate
- Domain logic remains independent from frameworks

---

## Digital Identity Format

The system generates a **unique 18-digit Digital ID**.

Structure:


EEE TTTTTTTTTTTTT SS


Where:

- `EEE` → Entity type code  
- `TTTTTTTTTTTTT` → Timestamp (milliseconds since custom epoch)  
- `SS` → Sequence number  

This design ensures:

- Uniqueness
- Ordering
- High generation throughput

---

## Stateless Login Concept

The authentication model explores a **stateless login design**.

The system keeps track of:

- Login attempts
- Device identification
- Authentication verification

Device tracking allows detection of unusual login activity and improves account security.

---

## Project Structure


```
src/main/java/com/giggr

domain/
  identity/
    DigitalIdentity.java
    Verification.java
    Consent.java
    DigitalIdGenerator.java

application/
  IdentityService.java

infrastructure/
  persistence/
  configuration/
```


The structure separates:

- **Domain logic**
- **Application orchestration**
- **Infrastructure concerns**



---

## Technology Stack

- Java
- Spring Boot
- H2 Database
- Maven

The system currently runs using a **file-based H2 database** for simplicity.

---

## How to Run

Clone the repository:


git clone https://github.com/SAIKDEV42/identity-platform-experiment.git


Navigate to the project directory:


cd identity-platform-experiment


Run the application:


mvn spring-boot:run


---

## Current Features

Implemented features:

- Identity aggregate modeling
- OTP verification flow
- Digital identity generation logic
- Stateless login tracking
- Device tracking

---

## Future Work

Possible future extensions include:

- Campaign-based sign-up flows
- Organization onboarding
- Team member management
- Application workflows
- Approval and evaluation pipelines
