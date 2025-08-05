package com.smartcare.service;

import com.smartcare.dto.payment.PaymentMethodRequest;
import com.smartcare.dto.payment.PaymentMethodResponse;
import com.smartcare.model.PaymentMethod;
import com.smartcare.model.User;
import com.smartcare.repository.PaymentMethodRepository;
import com.smartcare.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    public PaymentMethodResponse addPaymentMethod(Long userId, PaymentMethodRequest request) throws StripeException {
        User user = getUserById(userId);
        
        // Retrieve Stripe payment method to get details
        com.stripe.model.PaymentMethod stripePaymentMethod = 
                com.stripe.model.PaymentMethod.retrieve(request.getStripePaymentMethodId());

        // Create customer if needed
        String customerId = getOrCreateStripeCustomer(user);

        // Attach payment method to customer
        stripePaymentMethod.attach(
                com.stripe.param.PaymentMethodAttachParams.builder()
                        .setCustomer(customerId)
                        .build()
        );

        // Create payment method record
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUser(user);
        paymentMethod.setType(request.getType());
        paymentMethod.setStripePaymentMethodId(request.getStripePaymentMethodId());

        // Extract card details if it's a card payment method
        if (stripePaymentMethod.getCard() != null) {
            com.stripe.model.PaymentMethod.Card card = stripePaymentMethod.getCard();
            paymentMethod.setCardLastFour(card.getLast4());
            paymentMethod.setCardBrand(card.getBrand());
            paymentMethod.setCardExpMonth(Math.toIntExact(card.getExpMonth()));
            paymentMethod.setCardExpYear(Math.toIntExact(card.getExpYear()));
        }

        // Set as default if requested or if it's the first payment method
        boolean shouldSetAsDefault = request.isSetAsDefault() || 
                !paymentMethodRepository.existsByUserAndIsDefaultTrueAndIsActiveTrue(user);
        
        if (shouldSetAsDefault) {
            setAsDefaultPaymentMethod(user, paymentMethod);
        }

        paymentMethod = paymentMethodRepository.save(paymentMethod);
        return convertToPaymentMethodResponse(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getUserPaymentMethods(Long userId) {
        User user = getUserById(userId);
        List<PaymentMethod> paymentMethods = paymentMethodRepository
                .findActivePaymentMethodsByUserIdOrderByDefault(userId);
        
        return paymentMethods.stream()
                .map(this::convertToPaymentMethodResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getDefaultPaymentMethod(Long userId) {
        User user = getUserById(userId);
        PaymentMethod defaultPaymentMethod = paymentMethodRepository
                .findByUserAndIsDefaultTrueAndIsActiveTrue(user)
                .orElseThrow(() -> new RuntimeException("No default payment method found"));
        
        return convertToPaymentMethodResponse(defaultPaymentMethod);
    }

    public void setAsDefaultPaymentMethod(Long userId, Long paymentMethodId) {
        User user = getUserById(userId);
        PaymentMethod paymentMethod = getPaymentMethodById(paymentMethodId);
        
        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        
        setAsDefaultPaymentMethod(user, paymentMethod);
    }

    public void removePaymentMethod(Long userId, Long paymentMethodId) throws StripeException {
        User user = getUserById(userId);
        PaymentMethod paymentMethod = getPaymentMethodById(paymentMethodId);
        
        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Detach from Stripe
        com.stripe.model.PaymentMethod stripePaymentMethod = 
                com.stripe.model.PaymentMethod.retrieve(paymentMethod.getStripePaymentMethodId());
        stripePaymentMethod.detach();

        // Soft delete - mark as inactive
        paymentMethod.setActive(false);
        paymentMethodRepository.save(paymentMethod);

        // If this was the default payment method, set another one as default
        if (paymentMethod.isDefault()) {
            List<PaymentMethod> activePaymentMethods = paymentMethodRepository
                    .findByUserAndIsActiveTrue(user);
            
            if (!activePaymentMethods.isEmpty()) {
                PaymentMethod newDefault = activePaymentMethods.get(0);
                newDefault.setDefault(true);
                paymentMethodRepository.save(newDefault);
            }
        }
    }

    @Transactional(readOnly = true)
    public Long getActivePaymentMethodCount(Long userId) {
        User user = getUserById(userId);
        return paymentMethodRepository.countActivePaymentMethodsByUser(user);
    }

    public String getOrCreateStripeCustomer(User user) throws StripeException {
        // In a real implementation, you might store the Stripe customer ID in the User entity
        // For this example, we'll create a new customer each time or use email as identifier
        
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getFirstName() + " " + user.getLastName())
                .putMetadata("user_id", user.getId().toString())
                .build();

        Customer customer = Customer.create(params);
        return customer.getId();
    }

    // Private helper methods
    private void setAsDefaultPaymentMethod(User user, PaymentMethod newDefault) {
        // Remove default flag from current default payment method
        paymentMethodRepository.findByUserAndIsDefaultTrueAndIsActiveTrue(user)
                .ifPresent(currentDefault -> {
                    currentDefault.setDefault(false);
                    paymentMethodRepository.save(currentDefault);
                });

        // Set new default
        newDefault.setDefault(true);
    }

    private PaymentMethodResponse convertToPaymentMethodResponse(PaymentMethod paymentMethod) {
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setId(paymentMethod.getId());
        response.setType(paymentMethod.getType());
        response.setCardLastFour(paymentMethod.getCardLastFour());
        response.setCardBrand(paymentMethod.getCardBrand());
        response.setCardExpMonth(paymentMethod.getCardExpMonth());
        response.setCardExpYear(paymentMethod.getCardExpYear());
        response.setBankName(paymentMethod.getBankName());
        response.setAccountHolderName(paymentMethod.getAccountHolderName());
        response.setDefault(paymentMethod.isDefault());
        response.setActive(paymentMethod.isActive());
        response.setCreatedAt(paymentMethod.getCreatedAt());
        return response;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private PaymentMethod getPaymentMethodById(Long paymentMethodId) {
        return paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));
    }
} 