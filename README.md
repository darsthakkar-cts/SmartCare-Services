# Smart Care Services

A comprehensive healthcare management system built with Java 17 and Spring Boot 3.2.0, providing user authentication, doctor discovery, appointment booking, medication management, and health monitoring capabilities.

## 🌟 Features

### 1. User Authentication & Onboarding
- **JWT-based Authentication**: Secure login and registration system
- **Profile Setup**: Comprehensive user profile with personal information and health conditions
- **Guided Tour**: Interactive "Take a Tour" feature for new users
- **OAuth2 Integration**: Social login support

### 2. Doctor Discovery & Appointment Booking
- **Advanced Search**: Find doctors by specialty, location, language, and rating
- **Real-Time Slot Booking**: Calendar view with instant appointment booking
- **Doctor Profiles**: Detailed information including qualifications, ratings, and reviews
- **Multiple Appointment Types**: In-person, virtual, and phone consultations

### 3. Medication Management
- **Smart Reminders**: Customizable alerts for medication timings and refill schedules
- **Prescription Tracking**: View active prescriptions with dosage instructions
- **Pharmacy Integration**: Direct ordering from partnered pharmacies
- **Medication History**: Complete tracking of past and current medications

### 4. Health Monitoring & AI Insights
- **Real-time Health Data**: Track vital signs like heart rate, glucose levels
- **AI-driven Insights**: Smart health alerts and recommendations
- **Health Profile**: Comprehensive medical history and conditions
- **Telemedicine Integration**: Virtual consultation capabilities

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (development), MySQL (production)
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd smartcare-services
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api/v1`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`

### Default Credentials

The application comes with pre-seeded data for testing:

**Admin User:**
- Username: `admin`
- Password: `admin123`

**Test User:**
- Username: `testuser`
- Password: `test123`

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/v1/auth/signin` - User login
- `POST /api/v1/auth/signup` - User registration
- `GET /api/v1/auth/check-username` - Check username availability
- `GET /api/v1/auth/check-email` - Check email availability

### Profile Management
- `GET /api/v1/profile` - Get user profile
- `PUT /api/v1/profile` - Update user profile
- `GET /api/v1/profile/health` - Get health profile
- `POST /api/v1/profile/health` - Create/update health profile
- `POST /api/v1/profile/complete-tour` - Mark tour as completed

### Doctor Discovery
- `GET /api/v1/doctors/search` - Search doctors with filters
- `GET /api/v1/doctors/{id}` - Get doctor details
- `GET /api/v1/doctors/by-specialization` - Find doctors by specialty

### Appointment Management
- `POST /api/v1/appointments/book` - Book new appointment
- `GET /api/v1/appointments/my-appointments` - Get user appointments
- `GET /api/v1/appointments/upcoming` - Get upcoming appointments
- `GET /api/v1/appointments/available-slots` - Get available time slots
- `PUT /api/v1/appointments/{id}/status` - Update appointment status

### Medication Management
- `POST /api/v1/medications` - Add medication
- `GET /api/v1/medications` - Get user medications
- `GET /api/v1/medications/active` - Get active medications
- `PUT /api/v1/medications/{id}/status` - Update medication status
- `GET /api/v1/medications/refill-needed` - Get medications needing refill

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/smartcare/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # Entity classes
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # Security configuration
│   │   └── service/        # Business logic services
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/                   # Test classes
```

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Access Control**: Different permissions for users, doctors, and admins
- **Password Encryption**: BCrypt hashing for secure password storage
- **CORS Configuration**: Cross-origin request handling
- **Input Validation**: Comprehensive request validation

## 🗄️ Database Schema

### Core Entities
- **Users**: User authentication and profile data
- **Doctors**: Healthcare provider information
- **Appointments**: Booking and scheduling data
- **Medications**: Prescription and reminder data
- **HealthProfiles**: Medical history and conditions

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

## 📝 Configuration

### Database Configuration
```properties
# H2 Database (Development)
spring.datasource.url=jdbc:h2:mem:smartcare
spring.datasource.username=sa
spring.datasource.password=

# MySQL (Production)
spring.datasource.url=jdbc:mysql://localhost:3306/smartcare
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### JWT Configuration
```properties
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000
```

## 🚀 Deployment

### Docker Deployment (Coming Soon)
```bash
docker build -t smartcare-services .
docker run -p 8080:8080 smartcare-services
```

### Production Considerations
- Update JWT secret key
- Configure MySQL database
- Set up SSL/TLS certificates
- Configure email service for notifications
- Set up monitoring and logging

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions, please contact:
- Email: support@smartcare.com
- Documentation: [API Documentation](http://localhost:8080/swagger-ui.html)

## 🛣️ Roadmap

- [ ] Mobile app integration
- [ ] Real-time notifications
- [ ] Telemedicine video calls
- [ ] AI health insights
- [ ] Pharmacy integration
- [ ] Insurance verification
- [ ] Multi-language support
- [ ] Advanced analytics dashboard
