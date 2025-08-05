package com.smartcare.service;

import com.smartcare.config.PaymentConfig;
import com.smartcare.dto.payment.InvoiceResponse;
import com.smartcare.dto.payment.PaymentResponse;
import com.smartcare.model.*;
import com.smartcare.repository.AppointmentRepository;
import com.smartcare.repository.InvoiceRepository;
import com.smartcare.repository.PaymentRepository;
import com.smartcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private NotificationService notificationService;

    public InvoiceResponse createInvoiceForAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Check if invoice already exists
        if (invoiceRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new RuntimeException("Invoice already exists for this appointment");
        }

        Invoice invoice = new Invoice();
        invoice.setUser(appointment.getPatient());
        invoice.setAppointment(appointment);
        invoice.setSubtotal(BigDecimal.valueOf(appointment.getConsultationFee()));
        invoice.setTotalAmount(calculateTotalAmount(invoice.getSubtotal(), BigDecimal.ZERO, BigDecimal.ZERO));
//        invoice.setDescription("Consultation fee for appointment with Dr. " +
//                             appointment.getDoctor().getUser().getFirstName() + " " +
//                             appointment.getDoctor().getUser().getLastName());
//        invoice.setCurrency(paymentConfig.getDefaultCurrency());
        invoice.setStatus(InvoiceStatus.PENDING);

        invoice = invoiceRepository.save(invoice);

        // Send notification
        notificationService.sendInvoiceGeneratedNotification(invoice);

        return convertToInvoiceResponse(invoice);
    }

    public InvoiceResponse createCustomInvoice(Long userId, String description, BigDecimal amount, 
                                             BigDecimal taxAmount, BigDecimal discountAmount) {
        User user = getUserById(userId);

        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoice.setSubtotal(amount);
        invoice.setTaxAmount(taxAmount != null ? taxAmount : BigDecimal.ZERO);
        invoice.setDiscountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO);
        invoice.setTotalAmount(calculateTotalAmount(invoice.getSubtotal(), invoice.getTaxAmount(), invoice.getDiscountAmount()));
        invoice.setDescription(description);
        invoice.setCurrency(paymentConfig.getDefaultCurrency());
        invoice.setStatus(InvoiceStatus.PENDING);

        invoice = invoiceRepository.save(invoice);

        // Send notification
        notificationService.sendInvoiceGeneratedNotification(invoice);

        return convertToInvoiceResponse(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getUserInvoices(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        Page<Invoice> invoices = invoiceRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return invoices.map(this::convertToInvoiceResponse);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long userId, Long invoiceId) {
        User user = getUserById(userId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (!invoice.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToInvoiceResponseWithPayments(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return convertToInvoiceResponseWithPayments(invoice);
    }

    public void processPayment(Long invoiceId, BigDecimal paymentAmount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        BigDecimal newPaidAmount = invoice.getPaidAmount().add(paymentAmount);
        invoice.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidDate(LocalDateTime.now());
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIAL_PAYMENT);
        }

        invoiceRepository.save(invoice);
    }

    public void cancelInvoice(Long userId, Long invoiceId, String reason) {
        User user = getUserById(userId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (!invoice.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot cancel paid invoice");
        }

        if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot cancel invoice with payments");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setNotes(invoice.getNotes() + "\nCancelled: " + reason);
        invoiceRepository.save(invoice);
    }

    public void processOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(
                InvoiceStatus.PENDING, LocalDateTime.now());

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);

            // Send overdue notification
            notificationService.sendInvoiceOverdueNotification(invoice);
        }
    }

    public void sendInvoiceReminders() {
        LocalDateTime reminderDate = LocalDateTime.now().plusDays(3); // 3 days before due
        List<Invoice> invoicesNeedingReminder = invoiceRepository.findInvoicesNeedingReminder(reminderDate);

        for (Invoice invoice : invoicesNeedingReminder) {
            notificationService.sendInvoiceReminderNotification(invoice);
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByUserAndStatus(Long userId, InvoiceStatus status) {
        User user = getUserById(userId);
        BigDecimal total = invoiceRepository.getTotalAmountByUserAndStatus(user, status);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long getInvoiceCountByUserAndStatus(Long userId, InvoiceStatus status) {
        User user = getUserById(userId);
        return invoiceRepository.countByUserAndStatus(user, status);
    }

    // Private helper methods
    private BigDecimal calculateTotalAmount(BigDecimal subtotal, BigDecimal taxAmount, BigDecimal discountAmount) {
        return subtotal.add(taxAmount).subtract(discountAmount);
    }

    private InvoiceResponse convertToInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        populateBasicInvoiceFields(response, invoice);
        return response;
    }

    private InvoiceResponse convertToInvoiceResponseWithPayments(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        populateBasicInvoiceFields(response, invoice);

        // Add payment history
        List<Payment> payments = paymentRepository.findByInvoiceOrderByCreatedAtDesc(invoice);
        List<PaymentResponse> paymentResponses = payments.stream()
                .map(this::convertToPaymentResponse)
                .collect(Collectors.toList());
        response.setPayments(paymentResponses);

        return response;
    }

    private void populateBasicInvoiceFields(InvoiceResponse response, Invoice invoice) {
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setStatus(invoice.getStatus());
        response.setSubtotal(invoice.getSubtotal());
        response.setTaxAmount(invoice.getTaxAmount());
        response.setDiscountAmount(invoice.getDiscountAmount());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setPaidAmount(invoice.getPaidAmount());
        response.setRemainingAmount(invoice.getRemainingAmount());
        response.setCurrency(invoice.getCurrency());
        response.setDescription(invoice.getDescription());
        response.setNotes(invoice.getNotes());
        response.setDueDate(invoice.getDueDate());
        response.setPaidDate(invoice.getPaidDate());
        response.setCreatedAt(invoice.getCreatedAt());
        response.setOverdue(invoice.isOverdue());

        // Add appointment details if available
        if (invoice.getAppointment() != null) {
            Appointment appointment = invoice.getAppointment();
            response.setAppointmentId(appointment.getId());
            response.setAppointmentDateTime(appointment.getAppointmentDateTime());
//            response.setDoctorName(appointment.getDoctor().getUser().getFirstName() + " " +
//                                 appointment.getDoctor().getUser().getLastName());
        }
    }

    private PaymentResponse convertToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentReference(payment.getPaymentReference());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setTransactionFee(payment.getTransactionFee());
        response.setNetAmount(payment.getNetAmount());
        response.setCurrency(payment.getCurrency());
        response.setDescription(payment.getDescription());
        response.setFailureReason(payment.getFailureReason());
        response.setProcessedAt(payment.getProcessedAt());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 