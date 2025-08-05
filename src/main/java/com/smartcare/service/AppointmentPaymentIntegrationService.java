package com.smartcare.service;

import com.smartcare.model.Appointment;
import com.smartcare.model.AppointmentStatus;
import com.smartcare.model.Invoice;
import com.smartcare.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppointmentPaymentIntegrationService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Complete appointment and generate invoice
     */
//    public Invoice completeAppointmentAndGenerateInvoice(Long appointmentId) {
//        Appointment appointment = appointmentRepository.findById(appointmentId)
//                .orElseThrow(() -> new RuntimeException("Appointment not found"));
//
//        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
//            throw new RuntimeException("Only confirmed appointments can be completed");
//        }
//
//        // Mark appointment as completed
//        appointment.setStatus(AppointmentStatus.COMPLETED);
//        appointmentRepository.save(appointment);
//
//        // Generate invoice for the appointment
//        var invoiceResponse = invoiceService.createInvoiceForAppointment(appointmentId);
//
//        // The invoice service already sends a notification, but we can add appointment-specific logic here
//
//        return appointmentRepository.findById(appointmentId)
//                .map(Appointment::getId)
//                .map(invoiceService::createInvoiceForAppointment)
//                .map(response -> {
//                    // Additional logic can be added here if needed
//                    return null; // Return the actual invoice entity if needed
//                })
//                .orElse(null);
//    }

    /**
     * Handle appointment cancellation and invoice adjustments
     */
    public void handleAppointmentCancellation(Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Mark appointment as cancelled
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setNotes(appointment.getNotes() + "\nCancellation reason: " + reason);
        appointmentRepository.save(appointment);

        // Send cancellation notification
//        notificationService.sendAppointmentCancellationNotification(appointment);

        // If there's an existing invoice, handle it appropriately
        // (cancel if unpaid, process refund if paid, etc.)
        // This would require additional business logic based on your requirements
    }

    /**
     * Update appointment when payment is completed
     */
    public void handleAppointmentPaymentCompleted(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Update appointment status or add payment confirmation
        appointment.setNotes(appointment.getNotes() + "\nPayment completed successfully");
        appointmentRepository.save(appointment);

        // Send confirmation notification
//        notificationService.sendAppointmentConfirmationNotification(appointment);
    }
} 