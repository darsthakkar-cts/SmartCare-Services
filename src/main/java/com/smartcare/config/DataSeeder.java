package com.smartcare.config;

import com.smartcare.model.*;
import com.smartcare.repository.DoctorRepository;
import com.smartcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedDoctors();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            // Create admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@smartcare.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRoles(Collections.singleton(Role.ADMIN));
            admin.setEmailVerified(true);
            admin.setProfileCompleted(true);
            admin.setTourCompleted(true);
            userRepository.save(admin);

            // Create test user
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("test@smartcare.com");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setPhoneNumber("+1234567890");
            testUser.setDateOfBirth(LocalDateTime.of(1990, 5, 15, 0, 0));
            testUser.setGender(Gender.MALE);
            testUser.setRoles(Collections.singleton(Role.USER));
            testUser.setEmailVerified(true);
            testUser.setProfileCompleted(true);
            userRepository.save(testUser);

            System.out.println("Sample users created successfully!");
        }
    }

    private void seedDoctors() {
        if (doctorRepository.count() == 0) {
            // Create sample doctors
            Doctor doctor1 = new Doctor();
            doctor1.setFirstName("Dr. Sarah");
            doctor1.setLastName("Johnson");
            doctor1.setSpecialization("Cardiology");
            doctor1.setQualifications("MD, FACC");
            doctor1.setLicenseNumber("MD12345");
            doctor1.setPhoneNumber("+1234567891");
            doctor1.setEmail("sarah.johnson@smartcare.com");
            doctor1.setClinicAddress("123 Medical Center Dr");
            doctor1.setCity("New York");
            doctor1.setState("NY");
            doctor1.setZipCode("10001");
            doctor1.setLanguages(Arrays.asList("English", "Spanish"));
            doctor1.setRating(4.8);
            doctor1.setTotalReviews(156);
            doctor1.setConsultationFee(200.0);
            doctor1.setBio("Experienced cardiologist with over 15 years of practice in treating heart conditions.");
            doctor1.setIsActive(true);

            Doctor doctor2 = new Doctor();
            doctor2.setFirstName("Dr. Michael");
            doctor2.setLastName("Chen");
            doctor2.setSpecialization("Dermatology");
            doctor2.setQualifications("MD, FAAD");
            doctor2.setLicenseNumber("MD67890");
            doctor2.setPhoneNumber("+1234567892");
            doctor2.setEmail("michael.chen@smartcare.com");
            doctor2.setClinicAddress("456 Skin Care Ave");
            doctor2.setCity("Los Angeles");
            doctor2.setState("CA");
            doctor2.setZipCode("90001");
            doctor2.setLanguages(Arrays.asList("English", "Mandarin"));
            doctor2.setRating(4.6);
            doctor2.setTotalReviews(89);
            doctor2.setConsultationFee(150.0);
            doctor2.setBio("Board-certified dermatologist specializing in skin cancer detection and cosmetic procedures.");
            doctor2.setIsActive(true);

            Doctor doctor3 = new Doctor();
            doctor3.setFirstName("Dr. Emily");
            doctor3.setLastName("Rodriguez");
            doctor3.setSpecialization("Pediatrics");
            doctor3.setQualifications("MD, FAAP");
            doctor3.setLicenseNumber("MD11111");
            doctor3.setPhoneNumber("+1234567893");
            doctor3.setEmail("emily.rodriguez@smartcare.com");
            doctor3.setClinicAddress("789 Children's Way");
            doctor3.setCity("Chicago");
            doctor3.setState("IL");
            doctor3.setZipCode("60601");
            doctor3.setLanguages(Arrays.asList("English", "Spanish"));
            doctor3.setRating(4.9);
            doctor3.setTotalReviews(234);
            doctor3.setConsultationFee(120.0);
            doctor3.setBio("Compassionate pediatrician dedicated to providing comprehensive care for children.");
            doctor3.setIsActive(true);

            Doctor doctor4 = new Doctor();
            doctor4.setFirstName("Dr. James");
            doctor4.setLastName("Wilson");
            doctor4.setSpecialization("Internal Medicine");
            doctor4.setQualifications("MD, FACP");
            doctor4.setLicenseNumber("MD22222");
            doctor4.setPhoneNumber("+1234567894");
            doctor4.setEmail("james.wilson@smartcare.com");
            doctor4.setClinicAddress("321 Health Plaza");
            doctor4.setCity("Houston");
            doctor4.setState("TX");
            doctor4.setZipCode("77001");
            doctor4.setLanguages(List.of("English"));
            doctor4.setRating(4.7);
            doctor4.setTotalReviews(178);
            doctor4.setConsultationFee(180.0);
            doctor4.setBio("Internal medicine physician with expertise in preventive care and chronic disease management.");
            doctor4.setIsActive(true);

            doctorRepository.saveAll(Arrays.asList(doctor1, doctor2, doctor3, doctor4));
            
            System.out.println("Sample doctors created successfully!");
        }
    }
}
