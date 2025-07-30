# OrganLink - Organ Donation Management System

A comprehensive multi-tenant organ donation management system designed for hospitals to manage donors, recipients, and organ matching processes with AI-powered algorithms.

## 🏥 System Overview

OrganLink provides a secure, multi-tenant platform where hospitals can:
- Manage organ donors and recipients
- Track organ availability and requirements
- Use AI-powered matching algorithms for organ compatibility
- Access cross-hospital matching capabilities
- Maintain comprehensive medical records with audit trails

## 🏗️ Architecture

### Backend (Java Spring Boot)
- **Framework**: Spring Boot 3.2.1 with Java 17
- **Database**: PostgreSQL with multi-tenant architecture
- **Authentication**: JWT-based with Spring Security
- **API**: RESTful APIs with OpenAPI/Swagger documentation
- **AI Integration**: Spring AI with DeepLearning4J for matching algorithms
- **Validation**: Bean Validation with custom validators

### Frontend (React - To be implemented)
- **Framework**: React with TypeScript and Vite
- **Styling**: Tailwind CSS with medical-appropriate color scheme
- **State Management**: React Context API / Redux Toolkit
- **Routing**: React Router for SPA navigation

### AI Integration
- **Matching Algorithm**: DeepLearning4J models for organ compatibility
- **Cross-Hospital Matching**: Inter-hospital organ matching system
- **Spring AI**: Integration with OpenAI for advanced matching logic

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Database Setup
1. **Create PostgreSQL Database**:
   ```bash
   # Connect to PostgreSQL
   psql -U postgres -h localhost
   
   # Create database
   CREATE DATABASE organlink_db WITH OWNER = postgres;
   
   # Exit
   \q
   ```

2. **Verify Connection**:
   ```bash
   psql -U postgres -h localhost -d organlink_db -c "SELECT version();"
   ```

### Application Setup
1. **Clone and Build**:
   ```bash
   cd organLink
   mvn clean install
   ```

2. **Configure Database**:
   - Database URL: `jdbc:postgresql://localhost:5432/organlink_db`
   - Username: `postgres`
   - Password: `12345`
   - (Already configured in `application.properties`)

3. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Verify Setup**:
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

## 📋 API Endpoints

### Hospital Management
- `POST /api/v1/hospitals` - Create hospital
- `GET /api/v1/hospitals` - Get all hospitals (with pagination/filtering)
- `GET /api/v1/hospitals/{id}` - Get hospital by ID
- `GET /api/v1/hospitals/code/{code}` - Get hospital by code
- `PUT /api/v1/hospitals/{id}` - Update hospital
- `DELETE /api/v1/hospitals/{id}` - Delete hospital (soft delete)

### Location Management
- `POST /api/v1/locations/countries` - Create country
- `GET /api/v1/locations/countries` - Get all countries
- `GET /api/v1/locations/countries/{id}` - Get country by ID
- `GET /api/v1/locations/hierarchy` - Get complete location hierarchy

## 🗂️ Project Structure

```
organLink/
├── src/main/java/com/organlink/
│   ├── controller/          # REST Controllers
│   ├── service/             # Business Logic Services
│   ├── repository/          # Data Access Layer
│   ├── model/
│   │   ├── entity/          # JPA Entities
│   │   └── dto/             # Data Transfer Objects
│   ├── config/              # Configuration Classes
│   ├── security/            # Security Configuration
│   ├── ai/                  # AI/ML Components
│   ├── utils/               # Utility Classes
│   └── exception/           # Exception Handling
├── src/main/resources/
│   ├── application.properties
│   └── data.sql             # Initial seed data
├── src/test/java/           # Unit and Integration Tests
├── pom.xml                  # Maven dependencies
└── README.md
```

## 🔧 Technology Stack

### Core Framework
- **Spring Boot 3.2.1** - Main application framework
- **Spring Data JPA** - Database abstraction layer
- **Spring Security** - Authentication and authorization
- **Spring Validation** - Input validation

### Database & Persistence
- **PostgreSQL** - Primary database
- **Hibernate** - ORM framework
- **HikariCP** - Connection pooling

### API & Documentation
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **Jackson** - JSON serialization/deserialization
- **Bean Validation** - Request validation

### AI & Machine Learning
- **Spring AI** - AI integration framework
- **DeepLearning4J** - Deep learning library
- **ND4J** - Scientific computing library

### Testing
- **JUnit 5** - Unit testing framework
- **Spring Boot Test** - Integration testing
- **Testcontainers** - Database testing with containers

### Build & Development
- **Maven** - Build automation
- **MapStruct** - Bean mapping
- **SLF4J + Logback** - Logging framework

## 🏥 Hospital Module Features

### ✅ Completed Features
- **Complete CRUD Operations** for hospitals
- **Location Hierarchy** (Country → State → Hospital)
- **Multi-tenant Architecture** with tenant isolation
- **Advanced Search & Filtering** with pagination
- **Data Validation** with comprehensive error handling
- **API Documentation** with Swagger UI
- **Database Seeding** with initial data
- **Exception Handling** with consistent error responses

### 🔄 Hospital Management Capabilities
- Create hospitals with location assignment
- Search hospitals by name, code, or email
- Filter by state, country, or active status
- Paginated results with sorting options
- Soft delete functionality
- Tenant-based data isolation
- Comprehensive audit trails

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest="*IT"
```

### API Testing with Postman
1. Import the Postman collection (to be created)
2. Set base URL: `http://localhost:8080/api/v1`
3. Test all hospital management endpoints
4. Verify error handling and validation

## 🔒 Security Features

- **JWT Authentication** for secure API access
- **Role-based Authorization** (Doctor, Staff, Admin)
- **Input Validation** to prevent injection attacks
- **CORS Configuration** for cross-origin requests
- **Audit Logging** for compliance tracking
- **Data Encryption** for sensitive medical information

## 🚀 Next Development Phases

1. **Authentication System** - JWT login with role management
2. **User Management** - Doctor and staff account management
3. **Donor Module** - Donor registration and organ tracking
4. **Recipient Module** - Patient registration and requirements
5. **AI Matching Engine** - Advanced organ compatibility algorithms
6. **React Frontend** - Modern web interface
7. **Mobile App** - React Native mobile application

## 📊 Database Schema

The system uses a multi-tenant PostgreSQL database with the following key entities:
- **Countries** - Master country data
- **States** - State/province information
- **Hospitals** - Hospital details with tenant isolation
- **Users** - System users (doctors, staff, admins)
- **Donors** - Organ donor information
- **Recipients** - Organ recipient data
- **Matches** - AI-generated organ matches
- **Audit Logs** - Compliance and tracking data

## 🤝 Contributing

1. Follow Java coding standards and Spring Boot best practices
2. Write comprehensive unit and integration tests
3. Use proper exception handling and validation
4. Document all API endpoints with OpenAPI annotations
5. Follow the established project structure
6. Ensure medical data compliance (HIPAA)

## 📄 License

MIT License - see LICENSE file for details.

## 🆘 Support

For technical support or questions:
- Check the API documentation at `/swagger-ui.html`
- Review the database setup guide in `database_setup.md`
- Contact the development team for assistance

---

**OrganLink** - Saving lives through technology and compassion 🫀
