package com.smartcare.dto.payment;

import com.smartcare.model.PaymentMethodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentMethodRequest {
    
    @NotNull(message = "Payment method type is required")
    private PaymentMethodType type;
    
    @NotBlank(message = "Stripe payment method ID is required")
    private String stripePaymentMethodId;
    
    private boolean setAsDefault = false;

    // Getters and Setters
    public PaymentMethodType getType() { return type; }
    public void setType(PaymentMethodType type) { this.type = type; }

    public String getStripePaymentMethodId() { return stripePaymentMethodId; }
    public void setStripePaymentMethodId(String stripePaymentMethodId) { this.stripePaymentMethodId = stripePaymentMethodId; }

    public boolean isSetAsDefault() { return setAsDefault; }
    public void setSetAsDefault(boolean setAsDefault) { this.setAsDefault = setAsDefault; }
} 