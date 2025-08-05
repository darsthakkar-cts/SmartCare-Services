package com.smartcare.service;

import com.smartcare.dto.notification.NotificationResponse;
import com.smartcare.model.*;
import com.smartcare.repository.NotificationRepository;
import com.smartcare.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Notification creation methods
    public NotificationResponse createNotification(Long userId, NotificationType type, String title, 
                                                 String message, Integer priority, String actionUrl) {
        User user = getUserById(userId);
        
        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(priority != null ? priority : 1);
        notification.setActionUrl(actionUrl);
        
        notification = notificationRepository.save(notification);
        
        // Send email notification asynchronously if enabled
        if (emailEnabled && shouldSendEmail(type)) {
            sendEmailNotificationAsync(notification);
        }
        
        return convertToNotificationResponse(notification);
    }

    public void sendPaymentSuccessNotification(Payment payment) {
        String title = "Payment Successful";
        String message = String.format("Your payment of $%.2f for invoice #%s has been processed successfully.",
                payment.getAmount(), payment.getInvoice().getInvoiceNumber());
        
        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", payment.getId());
        data.put("invoiceId", payment.getInvoice().getId());
        data.put("amount", payment.getAmount());
        
        createNotificationWithData(payment.getUser().getId(), NotificationType.PAYMENT_SUCCESS, 
                title, message, 2, "/payments/" + payment.getId(), data);
    }

    public void sendPaymentFailedNotification(Payment payment) {
        String title = "Payment Failed";
        String message = String.format("Your payment of $%.2f for invoice #%s failed. Please try again or use a different payment method.",
                payment.getAmount(), payment.getInvoice().getInvoiceNumber());
        
        if (payment.getFailureReason() != null) {
            message += " Reason: " + payment.getFailureReason();
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", payment.getId());
        data.put("invoiceId", payment.getInvoice().getId());
        data.put("failureReason", payment.getFailureReason());
        
        createNotificationWithData(payment.getUser().getId(), NotificationType.PAYMENT_FAILED, 
                title, message, 3, "/invoices/" + payment.getInvoice().getId(), data);
    }

    public void sendInvoiceGeneratedNotification(Invoice invoice) {
        String title = "New Invoice Generated";
        String message = String.format("A new invoice #%s for $%.2f has been generated and is ready for payment.",
                invoice.getInvoiceNumber(), invoice.getTotalAmount());
        
        Map<String, Object> data = new HashMap<>();
        data.put("invoiceId", invoice.getId());
        data.put("amount", invoice.getTotalAmount());
        data.put("dueDate", invoice.getDueDate());
        
        createNotificationWithData(invoice.getUser().getId(), NotificationType.INVOICE_GENERATED, 
                title, message, 2, "/invoices/" + invoice.getId(), data);
    }

    public void sendInvoiceOverdueNotification(Invoice invoice) {
        String title = "Invoice Overdue";
        String message = String.format("Invoice #%s for $%.2f is now overdue. Please make payment to avoid late fees.",
                invoice.getInvoiceNumber(), invoice.getRemainingAmount());
        
        Map<String, Object> data = new HashMap<>();
        data.put("invoiceId", invoice.getId());
        data.put("overdueAmount", invoice.getRemainingAmount());
        data.put("dueDate", invoice.getDueDate());
        
        createNotificationWithData(invoice.getUser().getId(), NotificationType.INVOICE_OVERDUE, 
                title, message, 4, "/invoices/" + invoice.getId(), data);
    }

    public void sendInvoiceReminderNotification(Invoice invoice) {
        String title = "Payment Reminder";
        String message = String.format("Invoice #%s for $%.2f is due soon. Please make payment by %s.",
                invoice.getInvoiceNumber(), invoice.getRemainingAmount(), 
                invoice.getDueDate().toLocalDate());
        
        Map<String, Object> data = new HashMap<>();
        data.put("invoiceId", invoice.getId());
        data.put("amount", invoice.getRemainingAmount());
        data.put("dueDate", invoice.getDueDate());
        
        createNotificationWithData(invoice.getUser().getId(), NotificationType.INVOICE_GENERATED, 
                title, message, 2, "/invoices/" + invoice.getId(), data);
    }

//    public void sendAppointmentConfirmationNotification(Appointment appointment) {
//        String title = "Appointment Confirmed";
//        String message = String.format("Your appointment with Dr. %s %s on %s has been confirmed.",
//                appointment.getDoctor().getUser().getFirstName(),
//                appointment.getDoctor().getUser().getLastName(),
//                appointment.getAppointmentDateTime().toLocalDate());
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("appointmentId", appointment.getId());
//        data.put("doctorName", appointment.getDoctor().getUser().getFirstName() + " " +
//                              appointment.getDoctor().getUser().getLastName());
//        data.put("appointmentDateTime", appointment.getAppointmentDateTime());
//
//        createNotificationWithData(appointment.getPatient().getId(), NotificationType.APPOINTMENT_CONFIRMATION,
//                title, message, 2, "/appointments/" + appointment.getId(), data);
//    }

    // Notification retrieval methods
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        Page<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
        return notifications.map(this::convertToNotificationResponse);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        User user = getUserById(userId);
        List<Notification> notifications = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userId) {
        User user = getUserById(userId);
        return notificationRepository.countUnreadByRecipient(user);
    }

    // Notification actions
    public void markAsRead(Long userId, Long notificationId) {
        User user = getUserById(userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        User user = getUserById(userId);
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        
        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(Long userId, Long notificationId) {
        User user = getUserById(userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        notificationRepository.delete(notification);
    }

    // Background tasks
    public void cleanupExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository
                .findExpiredNotifications(LocalDateTime.now());
        
        notificationRepository.deleteAll(expiredNotifications);
    }

    @Transactional(readOnly = true)
    public void cleanupOldReadNotifications(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            notificationRepository.deleteOldReadNotifications(user, cutoffDate);
        }
    }

    // Private helper methods
    private void createNotificationWithData(Long userId, NotificationType type, String title, 
                                          String message, Integer priority, String actionUrl, 
                                          Map<String, Object> data) {
        User user = getUserById(userId);
        
        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(priority);
        notification.setActionUrl(actionUrl);
        
        try {
            notification.setData(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            // Log error but continue
            System.err.println("Error serializing notification data: " + e.getMessage());
        }
        
        notificationRepository.save(notification);
        
        // Send email notification asynchronously if enabled
        if (emailEnabled && shouldSendEmail(type)) {
            sendEmailNotificationAsync(notification);
        }
    }

    @Async
    private void sendEmailNotificationAsync(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notification.getRecipient().getEmail());
            message.setSubject(notification.getTitle());
            message.setText(notification.getMessage());
            
            mailSender.send(message);
            
            // Update notification status
            notification.setEmailSent(true);
            notificationRepository.save(notification);
            
        } catch (Exception e) {
            System.err.println("Error sending email notification: " + e.getMessage());
        }
    }

    private boolean shouldSendEmail(NotificationType type) {
        return switch (type) {
            case PAYMENT_SUCCESS, PAYMENT_FAILED, INVOICE_GENERATED, INVOICE_OVERDUE, 
                 APPOINTMENT_CONFIRMATION, APPOINTMENT_CANCELLATION -> true;
            default -> false;
        };
    }

    private NotificationResponse convertToNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setRead(notification.isRead());
        response.setPriority(notification.getPriority());
        response.setActionUrl(notification.getActionUrl());
        response.setExpiresAt(notification.getExpiresAt());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 