
## Lab 4: User Registration, Login, and Administration

This project extends the existing Chinook Album REST API with
Spring MVC, Thymeleaf, Bootstrap 5, and PostgreSQL user management.

### Requirements

- Java 23
- Maven (included through Maven Wrapper)
- PostgreSQL
- Existing Chinook database

### Local Startup

1. Start PostgreSQL.
2. Ensure the `chinook` database exists.
3. Configure database credentials in the Spring Boot application configuration.
4. Run `sql_scripts/users.sql` to create the users table.
5. Start the application:

```bash
./mvnw spring-boot:run
```

### Application Pages

- Home: http://localhost:8080/
- Registration: http://localhost:8080/register
- Login: http://localhost:8080/login
- Password Change: http://localhost:8080/change-password
- User Administration: http://localhost:8080/admin/users
- Album REST API: http://localhost:8080/api/albums

### Administrator Bootstrap

If no administrator exists, the application can create one at startup.

Set these environment variables before starting:

```bash
export LAB4_ADMIN_USERNAME=admin
export LAB4_ADMIN_PASSWORD=temporary-lab4-password
./mvnw spring-boot:run
```

Use a temporary demonstration password, never a personal password.

The administrator is created with:
- Role: ADMIN
- Enabled: true
- Password change required: true

The bootstrap process does not overwrite an existing administrator.

### Testing

Run:

```bash
./mvnw clean test
```

### Security Limitations

Lab 4 intentionally stores and compares passwords as plain text.
It uses temporary manual credential checks and does not provide
secure authentication or authorization.

Spring Security, BCrypt password hashing, authenticated sessions,
and role-based route protection are introduced in Lab 5.

Do not deploy this Lab 4 implementation to production.

