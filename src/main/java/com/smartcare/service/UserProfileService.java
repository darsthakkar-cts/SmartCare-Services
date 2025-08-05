package com.smartcare.service;

import com.smartcare.model.HealthProfile;
import com.smartcare.model.User;
import com.smartcare.repository.HealthProfileRepository;
import com.smartcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthProfileRepository healthProfileRepository;

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserProfile(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setGender(userDetails.getGender());
        user.setProfilePicture(userDetails.getProfilePicture());
        
        // Mark profile as completed if basic info is provided
        if (user.getFirstName() != null && user.getLastName() != null && 
            user.getDateOfBirth() != null && user.getGender() != null) {
            user.setProfileCompleted(true);
        }

        return userRepository.save(user);
    }

    public HealthProfile createOrUpdateHealthProfile(Long userId, HealthProfile healthProfileDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<HealthProfile> existingProfile = healthProfileRepository.findByUser(user);
        
        HealthProfile healthProfile;
        if (existingProfile.isPresent()) {
            healthProfile = existingProfile.get();
            updateHealthProfileFields(healthProfile, healthProfileDetails);
        } else {
            healthProfile = healthProfileDetails;
            healthProfile.setUser(user);
        }

        return healthProfileRepository.save(healthProfile);
    }

    private void updateHealthProfileFields(HealthProfile existing, HealthProfile details) {
        existing.setHeight(details.getHeight());
        existing.setWeight(details.getWeight());
        existing.setBloodType(details.getBloodType());
        existing.setMedicalConditions(details.getMedicalConditions());
        existing.setAllergies(details.getAllergies());
        existing.setEmergencyContactName(details.getEmergencyContactName());
        existing.setEmergencyContactPhone(details.getEmergencyContactPhone());
        existing.setEmergencyContactRelationship(details.getEmergencyContactRelationship());
        existing.setAdditionalNotes(details.getAdditionalNotes());
    }

    public Optional<HealthProfile> getHealthProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return healthProfileRepository.findByUser(user);
    }

    public User completeTour(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setTourCompleted(true);
        return userRepository.save(user);
    }

    public User updateProfilePicture(Long userId, String profilePictureUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setProfilePicture(profilePictureUrl);
        return userRepository.save(user);
    }
}
