# Identity & Authentication Prototype

Backend architecture experiment exploring scalable identity systems using **Java**, **Domain Driven Design (DDD)**, and **Clean Architecture**.

This repository contains an experimental backend implementation inspired by how large platforms design **identity and authentication subsystems**.

The project focuses on modeling **identity creation, OTP verification, digital identity generation, and stateless authentication concepts** while keeping the **domain logic independent from infrastructure frameworks**.

---

## Status

🚧 Experimental prototype

**Current focus:**

- Identity creation  
- OTP verification  
- Digital identity generation  
- Stateless authentication concepts  

This project is intended as an **architecture exploration**, not a production-ready identity service.

---

## Overview

Most large platforms rely on a dedicated **identity subsystem** responsible for:

- Creating and managing user identities  
- Verifying ownership of credentials  
- Generating globally unique identifiers  
- Managing authentication state  

This repository explores how such a subsystem can be designed using **Domain Driven Design** and **Clean Architecture principles**.

The implementation models the **foundational identity flow commonly required in modern platforms**.

---

## Architecture Approach

The project follows **Domain Driven Design (DDD)** and **Clean Architecture**.

The design emphasizes:

- Domain-first modeling  
- Aggregate-based lifecycle management  
- Explicit business invariants  
- Clear separation between domain and infrastructure layers  

The core of the system is the **DigitalIdentity aggregate**, which controls the lifecycle of identity creation and verification.

---

## Design Goals

This prototype explores backend architecture concepts commonly required in large-scale platforms:

- Designing identity systems with strong domain invariants  
- Generating globally unique and time-ordered digital identifiers  
- Separating domain logic from infrastructure frameworks  
- Modeling identity lifecycle and verification workflows  
- Supporting stateless authentication patterns  

---

## Identity Lifecycle

The identity subsystem models a controlled lifecycle for creating and activating identities.

```
User Signup Request
        ↓
Identity Created (Pending Verification)
        ↓
OTP Issued
        ↓
OTP Verified
        ↓
Digital ID Generated
        ↓
Identity Activated
        ↓
User Can Authenticate
```

### Lifecycle Rules Enforced by the Domain Model

- An identity begins in a **pending verification state**
- OTP verification confirms **ownership of credentials**
- Digital IDs are generated **only after successful verification**
- Identity activation allows the user to **authenticate**
- All lifecycle transitions are controlled by the **DigitalIdentity aggregate**

---

## Identity Subsystem Responsibilities

The identity subsystem manages:

- Identity creation  
- OTP-based verification  
- Digital identity generation  
- Identity lifecycle state transitions  

### Domain Invariants

- Digital IDs can only be generated **after OTP verification**
- Identity state transitions are controlled inside the aggregate
- Domain logic remains independent from infrastructure frameworks

---

## Digital Identity Format

The system generates a **unique 18-digit Digital ID**.

### Structure

```
EEE TTTTTTTTTTTTT SS
```

Where:

- `EEE` → Entity type code  
- `TTTTTTTTTTTTT` → Timestamp (milliseconds since custom epoch)  
- `SS` → Per-millisecond sequence number  

### Design Properties

- Globally unique identifiers  
- Natural ordering by creation time  
- High ID generation throughput  

 
ID generation is handled by a  **stateful domain service (`DigitalIdGenerator`)** , 
which ensures timestamp ordering and per-millisecond sequence management.

---

## Stateless Authentication Concept

 The authentication model explores a **stateless login approach** where 
authentication state is not stored in server sessions but inferred from 
verification and device metadata.

The system records metadata such as:

- Login attempts  
- Device identifiers  
- Verification state  

Device tracking can help detect unusual login behavior and improve account security.

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

### Domain Layer

- Contains business rules  
- Defines aggregates and domain services  

### Application Layer

- Orchestrates use cases  
- Coordinates domain operations  

### Infrastructure Layer

- Handles persistence  
- Provides framework integration  

---

## Technology Stack

- Java  
- Spring Boot  
- Maven  
- H2 Database  

The system currently uses a **file-based H2 database** for simplicity during experimentation.

---

## How to Run

### Clone the repository

```
git clone https://github.com/SAIKDEV42/identity-platform-experiment.git
```

### Navigate to the project directory

```
cd identity-platform-experiment
```

### Run the application

```
mvn spring-boot:run
```

---

## Implemented Concepts

This prototype currently explores several backend architecture ideas:

- Identity aggregate modeling  
- OTP verification workflow  
- Digital identity generation  
- Stateless authentication exploration  
- Device-based login tracking  

---

## Future Work

Possible extensions for this system include:

- Organization onboarding  
- Team member management  
- Role-based authorization  
- Identity federation  
- Multi-tenant identity support  
- Additional identity flows (minor / adult / organization)

---
