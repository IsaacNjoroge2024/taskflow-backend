# TaskFlow Backend

A RESTful API for task management built with Spring Boot and PostgreSQL. This backend provides comprehensive task CRUD operations, filtering, statistics, and deadline tracking capabilities.

## Features

- Create, read, update, and delete tasks
- Task filtering by status, priority, and due date
- Search tasks by title or description
- Track overdue tasks
- Task statistics and analytics
- Pagination and sorting support
- RESTful API design

## Technology Stack

- **Java 17**
- **Spring Boot 3.4.4** (Web, Data JPA, Validation)
- **PostgreSQL** - Primary database
- **Liquibase** - Database migration management
- **Gradle** - Build tool
- **Lombok** - Reduce boilerplate code
- **JUnit 5 & TestContainers** - Testing

## Prerequisites

Before running this application locally, ensure you have the following installed:

- **Java 17** or higher
- **PostgreSQL 12** or higher
- **Git**

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/taskflow-backend.git
cd taskflow-backend
```

### 2. Create Environment Configuration File

Create a `.env` file in the project root directory with your database password:

```bash
# .env file
PASSWORD=YourPassword
```

**Note:** The `.env` file is already included in `.gitignore` to prevent committing sensitive credentials to version control.

### 3. Set Up PostgreSQL Database

Create a PostgreSQL database and user for the application:

```sql
-- Connect to PostgreSQL as superuser (e.g., postgres)
CREATE DATABASE taskflow;
CREATE USER taskflow_user WITH PASSWORD 'YourPassword';
GRANT ALL PRIVILEGES ON DATABASE taskflow TO taskflow_user;

-- For PostgreSQL 15+, you may also need:
\c taskflow
GRANT ALL ON SCHEMA public TO taskflow_user;
```

### 4. Configure Application Properties

The application uses profile-based configuration and reads sensitive credentials from the `.env` file. For local development, it defaults to the `dev` profile.

**Development Configuration** (`src/main/resources/application-dev.properties`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskflow
spring.datasource.username=taskflow_user
spring.datasource.password=${PASSWORD}  # Loaded from .env file
```

The password is automatically loaded from your `.env` file using the spring-dotenv library. If you need to change other database settings (URL, username), update them in `application-dev.properties`.

### 5. Run Database Migrations

Liquibase will automatically run migrations when the application starts. Alternatively, you can run them manually:

```bash
./gradlew update
```

### 6. Build and Run the Application

**Using Gradle:**

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

**Using the JAR file:**

```bash
./gradlew build
java -jar build/libs/taskflow-backend.jar
```

The application will start on **http://localhost:8080**

The API base path is `/api`, so endpoints are accessible at **http://localhost:8080/api**

### 7. Verify the Application is Running

Open your browser or use curl to check the health endpoint:

```bash
curl http://localhost:8080/api/tasks/health
```

You should receive: `TaskFlow API is running!`

## API Endpoints

The API is accessible at `http://localhost:8080/api`

### Task Endpoints

- `GET /tasks` - Get all tasks (supports filtering, pagination, and sorting)
- `GET /tasks/{id}` - Get a specific task by ID
- `POST /tasks` - Create a new task
- `PUT /tasks/{id}` - Update an existing task
- `DELETE /tasks/{id}` - Delete a task
- `PATCH /tasks/{id}/toggle-completion` - Toggle task completion status
- `GET /tasks/overdue` - Get all overdue tasks
- `GET /tasks/statistics` - Get task statistics

### Example Request

**Create a Task:**

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete project documentation",
    "description": "Write comprehensive API documentation",
    "priority": "HIGH",
    "status": "PENDING",
    "dueDate": "2024-12-31"
  }'
```

**Get All Tasks:**

```bash
curl http://localhost:8080/api/tasks
```

## Running Tests

The project includes unit and integration tests using JUnit 5 and TestContainers.

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests com.taskflow.task.service.TaskServiceTest

# Run tests with coverage
./gradlew test jacocoTestReport
```

## Environment Variables

For production deployment, you can use the following environment variables:

- `DATABASE_URL` - PostgreSQL connection URL (format: `postgresql://username:password@host/database`)
- `SPRING_PROFILES_ACTIVE` - Active profile (dev/prod)

## Project Structure

```
src/
├── main/
│   ├── java/com/taskflow/
│   │   ├── TaskFlowApplication.java
│   │   ├── config/          # Configuration classes
│   │   ├── task/            # Task domain
│   │   │   ├── controller/  # REST controllers
│   │   │   ├── service/     # Business logic
│   │   │   ├── repository/  # Data access
│   │   │   ├── entity/      # JPA entities
│   │   │   ├── dto/         # Data Transfer Objects
│   │   │   └── enums/       # Enums
│   │   ├── common/          # Shared utilities
│   │   └── health/          # Health endpoints
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       ├── application-prod.properties
│       └── db/changelog/    # Liquibase migrations
└── test/                    # Test files
```

## Troubleshooting

### Database Connection Issues

If you encounter database connection errors:

1. Verify PostgreSQL is running: `sudo service postgresql status` (Linux) or `brew services list` (macOS)
2. Check database credentials in `application-dev.properties`
3. Ensure the database `taskflow` exists
4. Verify the user `taskflow_user` has proper permissions

### Port Already in Use

If port 8080 is already in use, you can change it in `application.properties`:

```properties
server.port=8081
```

## Future Implementations

To transform TaskFlow Backend into a fully professional, production-ready application, the following features and improvements are planned:

### 1. User Management & Authentication
- **Spring Security Integration** - Implement Spring Security for comprehensive security framework
- **User Registration API** - POST `/api/auth/register` with email verification
- **Login/Logout API** - JWT-based authentication with refresh tokens
- **User Entity & Repository** - Database models for user management
- **Password Encryption** - BCrypt password hashing
- **Email Verification Service** - JavaMailSender integration for account activation
- **Password Reset API** - Secure token-based password recovery
- **OAuth2 Integration** - Social login support (Google, GitHub, Microsoft)
- **Multi-factor Authentication (MFA)** - TOTP-based 2FA support

### 2. Authorization & Access Control
- **Task Ownership** - Add `userId` foreign key to Task entity
- **User-Task Relationship** - One-to-many relationship between User and Task
- **Spring Security Method Security** - `@PreAuthorize` annotations on service methods
- **Role-Based Access Control (RBAC)** - User roles (ADMIN, USER, GUEST)
- **Team/Workspace Entity** - Database schema for collaborative workspaces
- **Permission Management** - Fine-grained permissions system
- **Security Filter Chain** - Custom authentication and authorization filters

### 3. Collaboration Features
- **Task Sharing API** - Share tasks with specific users
- **Task Assignment** - Assign tasks to team members
- **Comments Entity** - One-to-many relationship with Task
- **Comment CRUD API** - POST/GET/PUT/DELETE `/api/tasks/{id}/comments`
- **Activity Log Entity** - Audit trail for task changes
- **Activity API** - GET `/api/tasks/{id}/activity`
- **WebSocket Support** - Real-time updates using Spring WebSocket
- **STOMP Messaging** - Publish task updates to subscribed clients

### 4. Advanced Task Management
- **Category Entity** - Many-to-many relationship with Task
- **Tag System** - Flexible tagging with `@ManyToMany` relationship
- **Recurring Tasks** - Cron expression support for task scheduling
- **Task Scheduler** - Spring `@Scheduled` jobs for recurring task creation
- **Subtask Entity** - Self-referencing Task relationship
- **File Upload API** - Multipart file handling with cloud storage (AWS S3, Azure Blob)
- **File Entity** - Store file metadata and URLs
- **Task Dependencies** - Graph-based dependency tracking
- **Task Templates** - Template entity and cloning API
- **Bulk Operations API** - PATCH `/api/tasks/bulk` for batch updates

### 5. Notifications & Reminders
- **Notification Entity** - Store notification history
- **Email Service** - JavaMailSender with templating (Thymeleaf)
- **Notification Scheduler** - Scheduled jobs for due date reminders
- **Push Notification Service** - Firebase Cloud Messaging integration
- **Notification Preferences** - User-configurable notification settings
- **Async Processing** - `@Async` methods for non-blocking email sending
- **Notification Queue** - RabbitMQ or Kafka for reliable message delivery

### 6. Data Management & Export
- **CSV Export API** - GET `/api/tasks/export/csv`
- **CSV Import API** - POST `/api/tasks/import/csv` with validation
- **Excel Support** - Apache POI integration for XLSX import/export
- **JSON Export** - Full data export in JSON format
- **Backup Service** - Scheduled database backups
- **Bulk Delete API** - Soft delete with archive functionality
- **Archive System** - `archived` flag on Task entity

### 7. Reports & Analytics
- **Statistics API Enhancement** - Detailed analytics endpoints
- **Task Metrics Service** - Completion rates, average completion time
- **Time Tracking Entity** - Log time spent on tasks
- **Reporting API** - GET `/api/reports/productivity`, `/api/reports/analytics`
- **Data Aggregation** - JPA criteria queries for complex reporting
- **Chart Data API** - Pre-aggregated data for frontend charts
- **Custom Report Builder** - Dynamic query building for custom reports

### 8. Performance & Scalability
- **Redis Caching** - Spring Cache with Redis for frequently accessed data
- **Query Optimization** - Add database indexes, optimize N+1 queries
- **Database Connection Pooling** - Fine-tune HikariCP settings
- **Pagination Improvements** - Cursor-based pagination for large datasets
- **API Response Compression** - GZIP compression for API responses
- **Database Partitioning** - Partition tasks table by date for better performance
- **Read Replicas** - Support for read-only database replicas
- **Async Processing** - CompletableFuture for long-running operations

### 9. Testing & Quality Assurance
- **Integration Tests** - `@SpringBootTest` with TestContainers for PostgreSQL
- **Controller Tests** - MockMvc tests for all endpoints
- **Repository Tests** - `@DataJpaTest` for all custom queries
- **Security Tests** - Test authentication and authorization
- **Code Coverage** - Achieve 80%+ coverage with JaCoCo
- **Performance Tests** - JMeter or Gatling load testing
- **Contract Testing** - Spring Cloud Contract for API contracts
- **Mutation Testing** - PITest for test quality validation

### 10. DevOps & Infrastructure
- **Docker Compose** - Multi-container development environment
- **CI/CD Pipeline** - GitHub Actions or Jenkins for automated builds
- **Health Checks** - Spring Actuator comprehensive health endpoints
- **Metrics Collection** - Micrometer with Prometheus integration
- **Distributed Tracing** - Spring Cloud Sleuth + Zipkin
- **Centralized Logging** - ELK Stack (Elasticsearch, Logstash, Kibana)
- **Configuration Management** - Spring Cloud Config Server
- **Container Orchestration** - Kubernetes deployment manifests
- **Monitoring Dashboards** - Grafana for application metrics

### 11. Security Enhancements
- **Rate Limiting** - Bucket4j or Resilience4j for API rate limiting
- **Input Validation** - Enhanced `@Valid` annotations and custom validators
- **SQL Injection Prevention** - Parameterized queries (already using JPA)
- **CSRF Protection** - Spring Security CSRF tokens for state-changing operations
- **CORS Configuration** - Environment-specific CORS policies
- **Security Headers** - Helmet-style security headers (CSP, X-Frame-Options)
- **API Key Authentication** - Support for API key-based access
- **Audit Logging** - Log all security-relevant events
- **Penetration Testing** - Regular security audits
- **Secrets Management** - HashiCorp Vault or AWS Secrets Manager integration

### 12. Business Features
- **Subscription Entity** - User subscription plans (Free, Pro, Enterprise)
- **Payment Integration** - Stripe API for subscription billing
- **Usage Tracking** - Monitor API usage and enforce limits
- **Feature Flags** - Toggle features based on subscription tier
- **Multi-tenancy** - Tenant isolation for enterprise customers
- **Internationalization (i18n)** - Message bundles for multiple languages
- **Public API** - OpenAPI/Swagger documentation
- **Webhooks** - Outgoing webhooks for task events
- **API Versioning** - Support for multiple API versions (/api/v1, /api/v2)
- **Third-party Integrations** - Slack, Discord, Microsoft Teams webhooks

### 13. Code Architecture Improvements
- **Microservices Architecture** - Split into task-service, user-service, notification-service
- **Event-Driven Architecture** - Spring Events or message queues for decoupling
- **CQRS Pattern** - Separate read and write models for complex domains
- **Hexagonal Architecture** - Clean architecture with ports and adapters
- **API Gateway** - Spring Cloud Gateway for routing and load balancing
- **Service Discovery** - Eureka or Consul for service registration
- **Circuit Breaker** - Resilience4j for fault tolerance
- **DTO Mapping** - MapStruct for efficient object mapping
- **Exception Handling** - Comprehensive error response structure
- **API Documentation** - Springdoc OpenAPI for interactive API docs

### Implementation Priority

**Phase 1 (Critical):**
- User authentication and authorization (Spring Security + JWT)
- Task ownership and user-task relationships
- Security enhancements (rate limiting, validation)
- Basic notification system (email)

**Phase 2 (High Priority):**
- User profile management APIs
- Collaboration features (sharing, comments, activity log)
- File upload and storage
- WebSocket for real-time updates

**Phase 3 (Medium Priority):**
- Advanced task features (tags, categories, recurring tasks, subtasks)
- Notification system enhancements (push notifications)
- Reports and analytics APIs
- Data import/export functionality

**Phase 4 (Future Enhancements):**
- Redis caching and performance optimization
- Microservices architecture migration
- Third-party integrations and webhooks
- Premium features and payment integration

## License

This project is part of the TaskFlow application suite.

## Support

For issues and questions, please create an issue in the GitHub repository.
