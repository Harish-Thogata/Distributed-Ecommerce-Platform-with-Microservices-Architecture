# 🛒 Distributed E-commerce Platform - Microservices Architecture

A scalable and distributed e-commerce platform built using Spring Boot microservices architecture. This project demonstrates real-world implementations of service discovery, load balancing, centralized API gateway, event-driven communication using Kafka, and secure transactions using JWT and RazorPay.

---

## ✨ Features

- 🔐 **JWT Authentication** - Secure login/registration with email verification.
- 🧾 **Order Management** - Orders created only after inventory validation.
- 📦 **Product & Inventory Sync** - Real-time updates using Kafka.
- 💳 **Payment Integration** - RazorPay integration for seamless transactions.
- 🔁 **Event-Driven Architecture** - Kafka used for service communication ensuring eventual consistency.
- 🚀 **Central Gateway** - API Gateway with routing, load balancing, and centralized Swagger UI.
- 🔍 **Service Discovery** - Eureka registry enables dynamic service resolution.
- 📈 **Monitoring** - Spring Boot Admin for real-time health and metrics tracking.
- 🐳 **Dockerized** - All services can be containerized and run using Docker.

---

## 🧱 Microservices Overview

### 1. **API Gateway**
- Built using Spring Cloud Gateway
- Load balancing, routing, centralized Swagger UI

### 2. **Eureka Service Registry**
- Service discovery for all microservices

### 3. **Spring Boot Admin**
- Centralized monitoring dashboard for microservices

### 4. **User Service**
- User registration and login with Spring Security
- JWT-based authentication
- Email verification via Spring Mail

### 5. **Product Service**
- CRUD operations for products
- Publishes Kafka events for stock updates

### 6. **Inventory Service**
- Maintains product stock levels
- Updates stock on order placement and payment confirmation

### 7. **Order Service**
- Validates inventory before creating orders (Feign Client)
- Publishes events to Payment Service
- Listens to payment status updates and adjusts inventory

### 8. **Payment Service**
- RazorPay integration
- Updates payment status and triggers inventory sync

---

## 🔁 Event-Driven Communication

Uses **Kafka** topics for the following interactions:

- Order → Inventory: Stock validation
- Order → Payment: Initiate payment
- Payment → Inventory: Update stock on success/failure
- Inventory → Product: Sync stock changes

---

## 🛠️ Tech Stack

| Category      | Technologies Used |
|---------------|-------------------|
| Backend       | Java, Spring Boot |
| Security      | Spring Security, JWT |
| Messaging     | Kafka |
| REST Clients  | Feign Client |
| Service Discovery | Eureka |
| Gateway       | Spring Cloud Gateway |
| Monitoring    | Spring Boot Admin |
| Email         | Spring Mail |
| Payments      | RazorPay |
| Database      | MySQL |
| Testing & Docs| Postman, Swagger |
| DevOps        | Docker, Git |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker
- Kafka & Zookeeper running
- MySQL instance running
