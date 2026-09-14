# Prismo Constructions - Backend

A robust Spring Boot REST API backend for Prismo Constructions, providing comprehensive project management capabilities with secure authentication, role-based access control, and advanced data management features.

## 🚀 Features

### Core Functionality
- **Secure Authentication**: JWT-based authentication with Spring Security
- **Role-Based Access Control**: Multi-level user roles (Admin, CEO, Project Manager, Client, Site Engineer)
- **Project Management**: Complete CRUD operations for construction projects
- **Task Management**: Create, assign, and track project tasks
- **Progress Tracking**: Log and monitor project progress with photo attachments
- **Issue Management**: Report, track, and resolve site issues
- **Approval Workflow**: Manage approval requests for project milestones
- **Document Management**: Upload and manage project documents
- **File Storage**: Handle file uploads with secure storage
- **Email Services**: Email notifications for important events
- **Inquiry Management**: Handle client inquiries and consultations
- **User Management**: Admin panel for user administration
- **Global Messaging**: Broadcast messages to all users

### Technical Features
- **RESTful API**: Clean, well-documented API endpoints
- **Database Integration**: MySQL with JPA/Hibernate ORM
- **Security**: Spring Security with JWT authentication
- **Validation**: Input validation with Spring Validation
- **Exception Handling**: Centralized error handling
- **CORS Support**: Cross-origin resource sharing configuration
- **Multipart File Upload**: Handle large file uploads (up to 50MB)

## 🛠️ Tech Stack

### Core Framework
- **Spring Boot 3.1.5**: Enterprise Java framework
- **Java 17**: Modern Java with enhanced features
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database abstraction layer
- **Spring Web**: REST API development
- **Spring Mail**: Email integration

### Database & ORM
- **MySQL 8.0+**: Relational database
- **Hibernate**: JPA implementation
- **Spring Data JPA**: Repository pattern abstraction

### Security
- **JWT (JSON Web Tokens)**: Stateless authentication
- **jjwt 0.11.5**: JWT library for token generation/validation
- **BCrypt**: Password encryption

### Utilities
- **Lombok**: Reduce boilerplate code
- **Spring Validation**: Input validation
- **Maven**: Build and dependency management

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: Version 3.6 or higher
- **MySQL Server**: Version 8.0 or higher
- **Git**: For version control

## 🔧 Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd prismo-construction/backend
```

### 2. Database Setup

#### Create MySQL Database
```sql
CREATE DATABASE prismo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Configure Database Connection
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/prismo_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## 📁 Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/prismo/backend/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── DatabaseSeeder.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/          # REST API controllers
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ClientController.java
│   │   │   │   ├── DocumentController.java
│   │   │   │   ├── FileUploadController.java
│   │   │   │   ├── GlobalMessageController.java
│   │   │   │   ├── InquiryController.java
│   │   │   │   ├── MilestoneController.java
│   │   │   │   ├── ProgressController.java
│   │   │   │   ├── ProjectController.java
│   │   │   │   ├── SiteIssueController.java
│   │   │   │   ├── TaskController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── AuthRequest.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   └── ...              # Other DTOs
│   │   │   ├── model/               # Entity models
│   │   │   │   ├── User.java
│   │   │   │   ├── Project.java
│   │   │   │   ├── Task.java
│   │   │   │   ├── ProgressLog.java
│   │   │   │   ├── SiteIssue.java
│   │   │   │   ├── ApprovalRequest.java
│   │   │   │   ├── Document.java
│   │   │   │   └── ...              # Other models
│   │   │   ├── repository/          # JPA repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ProjectRepository.java
│   │   │   │   ├── TaskRepository.java
│   │   │   │   └── ...              # Other repositories
│   │   │   ├── security/            # Security configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtAuthFilter.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── ProjectService.java
│   │   │   │   ├── TaskService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   └── ...              # Other services
│   │   │   └── BackendApplication.java
│   │   └── resources/
│   │       └── application.properties # Application configuration
├── uploads/                         # File upload directory
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## 🔐 Authentication & Security

### JWT Authentication Flow
1. User login → Server validates credentials
2. Server generates JWT token with user details
3. Client includes token in Authorization header
4. Server validates token on each request
5. Access granted based on user roles

### Security Configuration
- **Password Encryption**: BCrypt with strong hashing
- **Token Expiration**: 24 hours (configurable)
- **CORS**: Configured for frontend integration
- **Role-Based Access**: Method-level security with `@PreAuthorize`

### User Roles
- **ADMIN**: Full system access, user management
- **CEO**: Strategic oversight, consultations
- **PROJECT_MANAGER**: Project creation and management
- **CLIENT**: Project viewing, approvals
- **SITE_ENGINEER**: Progress reporting, issue tracking

## 🌐 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password

### Projects
- `GET /api/projects` - List all projects
- `GET /api/projects/{id}` - Get project details
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Tasks
- `GET /api/tasks` - List tasks
- `GET /api/tasks/{id}` - Get task details
- `POST /api/tasks` - Create task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Progress
- `GET /api/progress` - List progress logs
- `POST /api/progress` - Submit progress log
- `GET /api/progress/project/{projectId}` - Get project progress

### Issues
- `GET /api/issues` - List site issues
- `POST /api/issues` - Report new issue
- `PUT /api/issues/{id}` - Update issue
- `POST /api/issues/{id}/comments` - Add comment to issue

### Approvals
- `GET /api/approvals` - List approval requests
- `POST /api/approvals` - Create approval request
- `PUT /api/approvals/{id}` - Update approval status

### Documents
- `GET /api/documents` - List documents
- `POST /api/documents` - Upload document
- `GET /api/documents/{id}` - Download document
- `DELETE /api/documents/{id}` - Delete document

### Users (Admin)
- `GET /api/admin/users` - List all users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user

### Inquiries
- `GET /api/inquiries` - List inquiries
- `POST /api/inquiries` - Submit inquiry
- `PUT /api/inquiries/{id}` - Update inquiry status

## 🔧 Configuration

### Application Properties
Located in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/prismo_db
spring.datasource.username=root
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400000

# File Upload
file.upload-dir=uploads
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# Server Configuration
server.port=8080

# Mail Configuration
spring.mail.host=smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=your-email
spring.mail.password=your-password
```

### Environment-Specific Configuration
Create separate profiles for different environments:
- `application-dev.properties` - Development
- `application-prod.properties` - Production
- `application-test.properties` - Testing

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
```bash
mvn test jacoco:report
```

### Integration Tests
The project includes Spring Boot Test for integration testing:
- `@SpringBootTest` for full application context
- `@WebMvcTest` for controller testing
- `@DataJpaTest` for repository testing
- `MockMvc` for API endpoint testing

## 📦 Deployment

### Build for Production
```bash
mvn clean package -DskipTests
```

### Run JAR File
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### Docker Deployment

#### Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Build and Run
```bash
docker build -t prismo-backend .
docker run -p 8080:8080 prismo-backend
```

### Cloud Deployment

#### AWS EC2
1. Launch EC2 instance with Java 17
2. Install MySQL or use RDS
3. Deploy JAR file
4. Configure security groups
5. Set up environment variables

#### Heroku
```bash
# Create Procfile
echo "web: java -jar target/backend-0.0.1-SNAPSHOT.jar" > Procfile

# Deploy
git push heroku main
```

## 🔍 Database Schema

### Key Entities
- **User**: Authentication and user management
- **Project**: Construction project details
- **Task**: Project tasks and assignments
- **ProgressLog**: Daily progress tracking
- **SiteIssue**: Issue reporting and tracking
- **ApprovalRequest**: Approval workflow
- **Document**: File attachments
- **Inquiry**: Client inquiries
- **GlobalMessage**: System-wide announcements

### Relationships
- User → Projects (One-to-Many)
- Project → Tasks (One-to-Many)
- Project → ProgressLogs (One-to-Many)
- Project → SiteIssues (One-to-Many)
- Task → User (Many-to-One)
- Project → Documents (One-to-Many)

## 🐛 Troubleshooting

### Common Issues

**Database Connection Failed**
```bash
# Check MySQL service
sudo systemctl status mysql

# Verify credentials in application.properties
# Ensure database exists
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS prismo_db;"
```

**Port Already in Use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <pid> /F

# Kill process (Linux/Mac)
kill -9 <pid>
```

**JWT Token Issues**
- Verify JWT secret in configuration
- Check token expiration time
- Ensure proper token format in Authorization header

**File Upload Failures**
- Check upload directory permissions
- Verify file size limits in configuration
- Ensure sufficient disk space

**Maven Build Failures**
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

## 📝 Development Guidelines

### Code Style
- Follow Java naming conventions
- Use meaningful variable names
- Add Javadoc for public methods
- Keep methods focused and small

### API Design
- Use proper HTTP methods (GET, POST, PUT, DELETE)
- Return appropriate status codes
- Implement proper error handling
- Use DTOs for request/response

### Security Best Practices
- Never log sensitive information
- Validate all input data
- Use parameterized queries
- Implement proper CORS configuration
- Keep dependencies updated

### Performance
- Use pagination for large datasets
- Implement caching where appropriate
- Optimize database queries
- Use connection pooling

## 🔒 Security Considerations

### Production Deployment
- Change default JWT secret key
- Use strong database passwords
- Enable HTTPS/SSL
- Configure proper CORS settings
- Implement rate limiting
- Set up logging and monitoring
- Regular security updates

### Environment Variables
Use environment variables for sensitive data:
```bash
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_jwt_secret
export MAIL_PASSWORD=your_mail_password
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Review Process
- Ensure all tests pass
- Follow coding standards
- Update documentation
- Add tests for new features

## 📄 License

This project is proprietary software for Prismo Constructions.

## 📞 Support

For support and questions:
- Email: backend-support@prismo-constructions.com
- Documentation: [Internal Wiki]
- Issue Tracker: [Internal Jira]

## 🗺️ Roadmap

### Upcoming Features
- [ ] GraphQL API support
- [ ] Redis caching layer
- [ ] Advanced search and filtering
- [ ] Real-time notifications with WebSockets
- [ ] Enhanced audit logging
- [ ] API rate limiting
- [ ] Advanced analytics and reporting
- [ ] Integration with external services
- [ ] Mobile API optimization
- [ ] Enhanced security features (2FA, OAuth)

### Performance Improvements
- [ ] Database query optimization
- [ ] Response caching
- [ ] Async processing for heavy operations
- [ ] Load balancing support

---

**Built with ❤️ for Prismo Constructions**
