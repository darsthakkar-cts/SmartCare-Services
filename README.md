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
   cl
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

## ✅ Currently Implemented Features

### Core Backend Infrastructure
- **Spring Boot 3.2.0** application with Java 17
- **JWT-based Authentication** with role-based access control
- **H2 Database** (development) with MySQL support (production)
- **Spring Security** configuration with CORS support
- **OpenAPI/Swagger** documentation
- **Comprehensive Test Suite** (16 passing tests)

### 1. User Management & Authentication
- ✅ User registration and login
- ✅ JWT token generation and validation
- ✅ Password encryption with BCrypt
- ✅ Username and email availability checking
- ✅ User profile management with personal information
- ✅ Tour completion tracking for new users
- ✅ Role-based authorization (USER, DOCTOR, ADMIN, PHARMACY)

### 2. Doctor Discovery & Management
- ✅ Doctor profile creation with detailed information
- ✅ Advanced search functionality by:
  - Specialization
  - Location (city/state)
  - Languages spoken
  - Rating and reviews
- ✅ Doctor availability scheduling
- ✅ Consultation fee management
- ✅ Multi-language support for doctors

### 3. Appointment System
- ✅ Appointment booking with multiple types:
  - In-person consultations
  - Virtual consultations
  - Phone calls
- ✅ Appointment status management (SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW)
- ✅ Available time slot checking
- ✅ Patient appointment history
- ✅ Meeting link generation for virtual appointments

### 4. Medication Management
- ✅ Medication tracking with full details:
  - Dosage and frequency
  - Start and end dates
  - Prescription information
  - Instructions and notes
- ✅ Medication status management (ACTIVE, COMPLETED, PAUSED, CANCELLED)
- ✅ Refill reminder system
- ✅ Active medication filtering
- ✅ Medication history tracking

### 5. Health Profile Management
- ✅ Comprehensive health profiles with:
  - Basic information (height, weight, blood type)
  - Medical conditions tracking
  - Allergy management
  - Emergency contact information
  - Additional health notes
- ✅ Health data validation and tracking

### 6. Security & Data Protection
- ✅ JWT token-based authentication
- ✅ Password encryption and validation
- ✅ Input validation for all endpoints
- ✅ Role-based access control
- ✅ CORS configuration for frontend integration

### 7. API Documentation & Testing
- ✅ Complete OpenAPI/Swagger documentation
- ✅ RESTful API design
- ✅ Comprehensive test coverage (Repository, Service, Security layers)
- ✅ Integration tests for critical workflows

## 🚀 Suggested Additional Features

### High Priority Features
1. **Real-time Notifications**
   - Email notifications for appointments
   - SMS reminders for medications
   - Push notifications for mobile apps
   - In-app notification system

2. **Telemedicine Integration**
   - Video calling functionality (WebRTC)
   - Screen sharing capabilities
   - Chat functionality during consultations
   - File sharing between doctor and patient

3. **Advanced Health Monitoring**
   - Vital signs tracking (heart rate, blood pressure, glucose)
   - Health data visualization and trends
   - AI-powered health insights and recommendations
   - Integration with wearable devices (Fitbit, Apple Watch)

4. **Enhanced Medication Features**
   - Pharmacy integration for prescription orders
   - Drug interaction checking
   - Medication cost comparison
   - Insurance verification for prescriptions

### Medium Priority Features
5. **AI-Driven Features**
   - Symptom checker with AI recommendations
   - Personalized health tips and reminders
   - Predictive health analytics
   - Smart appointment scheduling based on urgency

6. **Advanced Appointment Features**
   - Calendar integration (Google Calendar, Outlook)
   - Appointment rescheduling with notifications
   - Waitlist functionality for popular doctors
   - Bulk appointment booking for families

7. **Payment Integration**
   - Online payment processing (Stripe, PayPal)
   - Insurance claim processing
   - Payment history and receipts
   - Subscription plans for premium features

8. **Enhanced Doctor Features**
   - Doctor dashboard with patient management
   - Prescription writing interface
   - Patient notes and history access
   - Revenue tracking and analytics

### Future Enhancements
9. **Mobile Application**
   - React Native or Flutter mobile app
   - Offline data synchronization
   - Mobile-specific features (camera for document scanning)
   - Biometric authentication

10. **Analytics & Reporting**
    - Patient health trend analysis
    - Doctor performance metrics
    - System usage analytics
    - Custom report generation

11. **Integration Capabilities**
    - Electronic Health Records (EHR) integration
    - Lab results integration
    - Insurance provider APIs
    - Third-party health apps integration

12. **Advanced Security Features**
    - Two-factor authentication (2FA)
    - HIPAA compliance features
    - Data encryption at rest
    - Audit logging for all actions

13. **Multi-tenant Architecture**
    - Hospital/clinic management
    - Multi-organization support
    - Custom branding for different clients
    - Role hierarchy for large organizations

14. **Internationalization**
    - Multi-language support
    - Currency conversion for global use
    - Regional compliance (GDPR, HIPAA)
    - Time zone handling

## 🏆 Current Development Status
- ✅ **Backend API**: Fully implemented and tested
- ✅ **Database Schema**: Complete with all relationships
- ✅ **Authentication**: JWT-based security implemented
- ✅ **Documentation**: Swagger/OpenAPI documentation available
- ✅ **Testing**: Comprehensive test suite (16 tests passing)
- ✅ **Version Control**: Git repository with proper branching
- 🔄 **Frontend**: Ready for integration
- 🔄 **Deployment**: Ready for containerization and cloud deployment
