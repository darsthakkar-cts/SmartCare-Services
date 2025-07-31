<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Smart Care Services - Copilot Instructions

This is a comprehensive healthcare management system built with Java 17 and Spring Boot 3.2.0. The application provides:

## Core Features
- **User Authentication & Onboarding**: JWT-based authentication with profile setup and guided tour
- **Doctor Discovery & Appointment Booking**: Search doctors by specialty, location, language, and rating with real-time slot booking
- **Medication Management**: Reminders, prescription tracking, and pharmacy integration
- **Health Monitoring**: Real-time health data tracking and AI-driven insights
- **Telemedicine Integration**: Virtual consultations and appointment management

## Technical Stack
- **Framework**: Spring Boot 3.2.0 with Spring Security, Spring Data JPA
- **Database**: H2 (development), MySQL (production)
- **Authentication**: JWT tokens with OAuth2 support
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

## Architecture Guidelines
- Follow RESTful API design principles
- Use DTOs for request/response objects
- Implement proper error handling and validation
- Apply security best practices with role-based access control
- Use service layer pattern for business logic
- Implement repository pattern for data access

## Security Configuration
- JWT-based authentication with role-based authorization
- Password encryption using BCrypt
- CORS and CSRF protection configured
- OAuth2 integration for social login

## Key Models
- **User**: Core user entity with authentication and profile data
- **Doctor**: Healthcare provider information with specializations and availability
- **Appointment**: Booking system with status management
- **Medication**: Prescription tracking with reminders
- **HealthProfile**: Medical history and health conditions

## API Endpoints Structure
- `/api/v1/auth/*` - Authentication endpoints
- `/api/v1/profile/*` - User profile management
- `/api/v1/doctors/*` - Doctor search and management
- `/api/v1/appointments/*` - Appointment booking and management
- `/api/v1/medications/*` - Medication tracking and reminders

When generating code:
1. Follow the existing project structure and naming conventions
2. Use proper validation annotations and error handling
3. Include Swagger/OpenAPI documentation annotations
4. Implement security checks where appropriate
5. Follow Spring Boot best practices for dependency injection
6. Use the established DTOs and response patterns
