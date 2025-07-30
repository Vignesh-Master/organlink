# Database Setup Instructions for OrganLink

## Prerequisites
- MySQL 8.0+ installed and running
- MySQL user with database creation privileges (root or dedicated user)

## Step 1: Create Database

### Option A: Using mysql command line
```bash
# Connect to MySQL as root user
mysql -u root -p

# Create the database
CREATE DATABASE organlink_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

# Grant privileges (if using dedicated user)
# GRANT ALL PRIVILEGES ON organlink_db.* TO 'organlink_user'@'localhost';

# Exit mysql
exit;
```

### Option B: Using SQL file
```bash
# Run the provided SQL script
mysql -u root -p < create_database.sql
```

## Step 2: Verify Database Connection

Test the connection with the credentials in application.properties:
```bash
mysql -u root -p organlink_db -e "SELECT VERSION();"
```

Expected output should show MySQL version information.

## Step 3: Start the Application

The Spring Boot application will automatically:
1. Create all necessary tables using JPA/Hibernate
2. Insert initial seed data (countries, states, organ types)
3. Set up indexes and constraints

## Database Schema Overview

The application will create the following tables:
- `countries` - Country master data
- `states` - State/province data linked to countries
- `hospitals` - Hospital information with multi-tenant support
- `organ_types` - Master data for organ types
- Additional tables for users, donors, recipients, matches (future modules)

## Connection Details

- **Host**: localhost
- **Port**: 3306 (default MySQL port)
- **Database**: organlink_db
- **Username**: root
- **Password**: 12345
- **URL**: jdbc:mysql://localhost:3306/organlink_db?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true

## Troubleshooting

### Connection Issues
1. Ensure MySQL service is running
2. Check if port 3306 is available
3. Verify username/password combination
4. Check MySQL configuration (my.cnf) for authentication method

### Permission Issues
```sql
-- Grant all privileges to root user
GRANT ALL PRIVILEGES ON organlink_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### Reset Database (if needed)
```sql
-- Drop and recreate database
DROP DATABASE IF EXISTS organlink_db;
CREATE DATABASE organlink_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Initial Data

The application will automatically insert:
- 3 countries (India, United States, United Kingdom)
- 8 states across these countries
- 8 organ types with compatibility factors

## Next Steps

After database setup:
1. Start the Spring Boot application: `mvn spring-boot:run`
2. Access Swagger UI: http://localhost:8080/swagger-ui.html
3. Test API endpoints using Postman or Swagger UI
4. Verify data creation through API calls
