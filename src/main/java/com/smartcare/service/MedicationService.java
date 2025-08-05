package com.smartcare.service;

import com.smartcare.model.Medication;
import com.smartcare.model.MedicationStatus;
import com.smartcare.model.User;
import com.smartcare.repository.MedicationRepository;
import com.smartcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private UserRepository userRepository;

    public Medication addMedication(Long userId, Medication medication) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        medication.setUser(user);
        return medicationRepository.save(medication);
    }

    public List<Medication> getUserMedications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return medicationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Medication> getActiveMedications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return medicationRepository.findActiveMedicationsByUser(user, LocalDateTime.now());
    }

    public Optional<Medication> getMedicationById(Long id) {
        return medicationRepository.findById(id);
    }

    public Medication updateMedication(Long id, Medication medicationDetails) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        medication.setMedicationName(medicationDetails.getMedicationName());
        medication.setDosage(medicationDetails.getDosage());
        medication.setFrequency(medicationDetails.getFrequency());
        medication.setStartDate(medicationDetails.getStartDate());
        medication.setEndDate(medicationDetails.getEndDate());
        medication.setReminderTimes(medicationDetails.getReminderTimes());
        medication.setInstructions(medicationDetails.getInstructions());
        medication.setStatus(medicationDetails.getStatus());
        medication.setRefillReminderDays(medicationDetails.getRefillReminderDays());
        medication.setRemainingQuantity(medicationDetails.getRemainingQuantity());
        medication.setPrescribedBy(medicationDetails.getPrescribedBy());

        return medicationRepository.save(medication);
    }

    public Medication updateMedicationStatus(Long id, MedicationStatus status) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found"));
        
        medication.setStatus(status);
        return medicationRepository.save(medication);
    }

    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }

    public List<Medication> getMedicationsNeedingRefill() {
        return medicationRepository.findMedicationsNeedingRefill();
    }

    public Medication updateRemainingQuantity(Long id, Integer quantity) {
        Medication medication = medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found"));
        
        medication.setRemainingQuantity(quantity);
        return medicationRepository.save(medication);
    }

    public List<Medication> getMedicationsByStatus(Long userId, MedicationStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return medicationRepository.findByUserAndStatus(user, status);
    }
}
