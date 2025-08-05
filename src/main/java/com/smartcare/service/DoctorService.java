package com.smartcare.service;

import com.smartcare.model.Doctor;
import com.smartcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public Page<Doctor> searchDoctors(String specialization, String city, String state, 
                                    String language, Double minRating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        
        if (language != null && !language.isEmpty()) {
            return doctorRepository.findByLanguage(language, pageable);
        }
        
        return doctorRepository.findDoctorsWithFilters(specialization, city, state, minRating, pageable);
    }

    public List<Doctor> getAllActiveDoctors() {
        return doctorRepository.findByIsActiveTrue();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationContainingIgnoreCase(specialization);
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setQualifications(doctorDetails.getQualifications());
        doctor.setPhoneNumber(doctorDetails.getPhoneNumber());
        doctor.setEmail(doctorDetails.getEmail());
        doctor.setClinicAddress(doctorDetails.getClinicAddress());
        doctor.setCity(doctorDetails.getCity());
        doctor.setState(doctorDetails.getState());
        doctor.setZipCode(doctorDetails.getZipCode());
        doctor.setLanguages(doctorDetails.getLanguages());
        doctor.setConsultationFee(doctorDetails.getConsultationFee());
        doctor.setBio(doctorDetails.getBio());
        doctor.setIsActive(doctorDetails.getIsActive());

        return doctorRepository.save(doctor);
    }
}
