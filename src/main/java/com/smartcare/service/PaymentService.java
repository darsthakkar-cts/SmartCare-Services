package com.smartcare.service;

import com.smartcare.config.PaymentConfig;
import com.smartcare.dto.payment.PaymentIntentResponse;
import com.smartcare.dto.payment.PaymentRequest;
import com.smartcare.dto.payment.PaymentResponse;
import com.smartcare.model.*;
import com.smartcare.repository.InvoiceRepository;
import com.smartcare.repository.PaymentMethodRepository;
import com.smartcare.repository.PaymentRepository;
import com.smartcare.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InvoiceService invoiceService;

    public PaymentIntentResponse createPaymentIntent(Long userId, PaymentRequest request) throws StripeException {
        User user = getUserById(userId);
        Invoice invoice = getInvoiceById(request.getInvoiceId());
        
        validatePaymentRequest(user, invoice, request);

        // Create payment record
        Payment payment = createPaymentRecord(user, invoice, request);
        
        // Calculate Stripe amount (in cents)
        long stripeAmount = request.getAmount().multiply(new BigDecimal(100)).longValue();
        
        // Get or determine payment method
        PaymentMethod paymentMethod = getPaymentMethodForRequest(user, request.getPaymentMethodId());
        
        // Create Stripe PaymentIntent
        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .setAmount(stripeAmount)
                .setCurrency(paymentConfig.getDefaultCurrency().toLowerCase())
//                .setDescription(generatePaymentDescription(invoice))
                .putMetadata("payment_id", payment.getId().toString())
                .putMetadata("invoice_id", invoice.getId().toString())
                .putMetadata("user_id", user.getId().toString());

        if (paymentMethod != null) {
            builder.setPaymentMethod(paymentMethod.getStripePaymentMethodId());
            builder.setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);
        }

        PaymentIntent paymentIntent = PaymentIntent.create(builder.build());

        // Update payment with Stripe details
        payment.setStripePaymentIntentId(paymentIntent.getId());
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        return new PaymentIntentResponse(
                paymentIntent.getClientSecret(),
                paymentIntent.getId(),
                request.getAmount(),
                paymentConfig.getDefaultCurrency(),
                paymentIntent.getStatus(),
                payment.getId()
        );
    }

    public PaymentResponse confirmPayment(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if ("succeeded".equals(paymentIntent.getStatus())) {
            return handleSuccessfulPayment(payment, paymentIntent);
        } else if ("requires_action".equals(paymentIntent.getStatus())) {
            return handlePendingPayment(payment, paymentIntent);
        } else {
            return handleFailedPayment(payment, paymentIntent);
        }
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getUserPayments(Long userId, Pageable pageable) {
        User user = getUserById(userId);
        Page<Payment> payments = paymentRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return payments.map(this::convertToPaymentResponse);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long userId, Long paymentId) {
        User user = getUserById(userId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (!payment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToPaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReference(String paymentReference) {
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToPaymentResponse(payment);
    }

    public void processWebhookEvent(String paymentIntentId, String eventType) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
            if (paymentOpt.isPresent()) {
                Payment payment = paymentOpt.get();
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                
                switch (eventType) {
                    case "payment_intent.succeeded":
                        if (payment.getStatus() != PaymentStatus.COMPLETED) {
                            handleSuccessfulPayment(payment, paymentIntent);
                        }
                        break;
                    case "payment_intent.payment_failed":
                        handleFailedPayment(payment, paymentIntent);
                        break;
                }
            }
        } catch (Exception e) {
            // Log error but don't throw to avoid webhook retry
            System.err.println("Error processing webhook: " + e.getMessage());
        }
    }

    public BigDecimal calculateTransactionFee(BigDecimal amount) {
        BigDecimal percentageFee = amount.multiply(paymentConfig.getTransaction().getFeePercentage())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        return percentageFee.add(paymentConfig.getTransaction().getFeeFixed());
    }

    // Private helper methods
    private PaymentResponse handleSuccessfulPayment(Payment payment, PaymentIntent paymentIntent) throws StripeException {
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());
        
//        // Get charge details for transaction fee
//        if (!paymentIntent.getCharges().getData().isEmpty()) {
//            Charge charge = paymentIntent.getCharges().getData().get(0);
//            payment.setStripeChargeId(charge.getId());
//
//            // Calculate actual transaction fee from Stripe
//            BigDecimal actualFee = new BigDecimal(charge.getBalanceTransaction().getFee())
//                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
//            payment.setTransactionFee(actualFee);
//            payment.setNetAmount(payment.getAmount().subtract(actualFee));
//        } else {
//            // Fallback to calculated fee
//            BigDecimal calculatedFee = calculateTransactionFee(payment.getAmount());
//            payment.setTransactionFee(calculatedFee);
//            payment.setNetAmount(payment.getAmount().subtract(calculatedFee));
//        }
        
        paymentRepository.save(payment);
        
        // Update invoice
        invoiceService.processPayment(payment.getInvoice().getId(), payment.getAmount());
        
        // Send notification
        notificationService.sendPaymentSuccessNotification(payment);
        
        return convertToPaymentResponse(payment);
    }

    private PaymentResponse handlePendingPayment(Payment payment, PaymentIntent paymentIntent) {
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        return convertToPaymentResponse(payment);
    }

    private PaymentResponse handleFailedPayment(Payment payment, PaymentIntent paymentIntent) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(paymentIntent.getLastPaymentError() != null ? 
                paymentIntent.getLastPaymentError().getMessage() : "Payment failed");
        paymentRepository.save(payment);
        
        // Send notification
        notificationService.sendPaymentFailedNotification(payment);
        
        return convertToPaymentResponse(payment);
    }

    private Payment createPaymentRecord(User user, Invoice invoice, PaymentRequest request) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setCurrency(paymentConfig.getDefaultCurrency());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    private PaymentMethod getPaymentMethodForRequest(User user, Long paymentMethodId) {
        if (paymentMethodId != null) {
            return paymentMethodRepository.findById(paymentMethodId)
                    .filter(pm -> pm.getUser().getId().equals(user.getId()) && pm.isActive())
                    .orElseThrow(() -> new RuntimeException("Payment method not found"));
        }
        return paymentMethodRepository.findByUserAndIsDefaultTrueAndIsActiveTrue(user).orElse(null);
    }

    private void validatePaymentRequest(User user, Invoice invoice, PaymentRequest request) {
        if (!invoice.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice is already paid");
        }
        
        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new RuntimeException("Cannot pay cancelled invoice");
        }
        
        if (request.getAmount().compareTo(invoice.getRemainingAmount()) > 0) {
            throw new RuntimeException("Payment amount exceeds remaining balance");
        }
    }

//    private String generatePaymentDescription(Invoice invoice) {
//        StringBuilder description = new StringBuilder("SmartCare Payment - ");
//        description.append("Invoice #").append(invoice.getInvoiceNumber());
//
//        if (invoice.getAppointment() != null) {
//            description.append(" for appointment with Dr. ")
//                    .append(invoice.getAppointment().getDoctor().getUser().getFirstName())
//                    .append(" ").append(invoice.getAppointment().getDoctor().getUser().getLastName());
//        }
//
//        return description.toString();
//    }

    private PaymentResponse convertToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentReference(payment.getPaymentReference());
        response.setInvoiceId(payment.getInvoice().getId());
        response.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
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

    private Invoice getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
} 