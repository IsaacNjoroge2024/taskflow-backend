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

## License

This project is part of the TaskFlow application suite.

## Support

For issues and questions, please create an issue in the GitHub repository.
