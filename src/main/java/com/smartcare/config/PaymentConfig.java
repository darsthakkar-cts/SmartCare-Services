package com.smartcare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private String defaultCurrency = "USD";
    private Transaction transaction = new Transaction();
    private Invoice invoice = new Invoice();
    private Retry retry = new Retry();

    public static class Transaction {
        private BigDecimal feePercentage = new BigDecimal("2.9");
        private BigDecimal feeFixed = new BigDecimal("0.30");

        // Getters and Setters
        public BigDecimal getFeePercentage() { return feePercentage; }
        public void setFeePercentage(BigDecimal feePercentage) { this.feePercentage = feePercentage; }

        public BigDecimal getFeeFixed() { return feeFixed; }
        public void setFeeFixed(BigDecimal feeFixed) { this.feeFixed = feeFixed; }
    }

    public static class Invoice {
        private Integer dueDays = 30;

        // Getters and Setters
        public Integer getDueDays() { return dueDays; }
        public void setDueDays(Integer dueDays) { this.dueDays = dueDays; }
    }

    public static class Retry {
        private Integer maxAttempts = 3;
        private Integer intervalHours = 24;

        // Getters and Setters
        public Integer getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

        public Integer getIntervalHours() { return intervalHours; }
        public void setIntervalHours(Integer intervalHours) { this.intervalHours = intervalHours; }
    }

    // Main Getters and Setters
    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public Retry getRetry() { return retry; }
    public void setRetry(Retry retry) { this.retry = retry; }
} 