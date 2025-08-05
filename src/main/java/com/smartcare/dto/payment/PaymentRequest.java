package com.smartcare.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PaymentRequest {
    
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    private BigDecimal amount;
    
    private Long paymentMethodId; // Optional - will use default if not provided
    
    private String description;
    
    private boolean savePaymentMethod = false;

    // Getters and Setters
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Long getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isSavePaymentMethod() { return savePaymentMethod; }
    public void setSavePaymentMethod(boolean savePaymentMethod) { this.savePaymentMethod = savePaymentMethod; }
} 