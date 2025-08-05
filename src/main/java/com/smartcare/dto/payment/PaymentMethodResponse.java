package com.smartcare.dto.payment;

import com.smartcare.model.PaymentMethodType;
import java.time.LocalDateTime;

public class PaymentMethodResponse {
    
    private Long id;
    private PaymentMethodType type;
    private String cardLastFour;
    private String cardBrand;
    private Integer cardExpMonth;
    private Integer cardExpYear;
    private String bankName;
    private String accountHolderName;
    private boolean isDefault;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PaymentMethodType getType() { return type; }
    public void setType(PaymentMethodType type) { this.type = type; }

    public String getCardLastFour() { return cardLastFour; }
    public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }

    public String getCardBrand() { return cardBrand; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }

    public Integer getCardExpMonth() { return cardExpMonth; }
    public void setCardExpMonth(Integer cardExpMonth) { this.cardExpMonth = cardExpMonth; }

    public Integer getCardExpYear() { return cardExpYear; }
    public void setCardExpYear(Integer cardExpYear) { this.cardExpYear = cardExpYear; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
} 