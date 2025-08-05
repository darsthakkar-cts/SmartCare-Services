package com.smartcare.repository;

import com.smartcare.model.Payment;
import com.smartcare.model.PaymentStatus;
import com.smartcare.model.User;
import com.smartcare.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentReference(String paymentReference);
    
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    Page<Payment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Payment> findByInvoiceOrderByCreatedAtDesc(Invoice invoice);
    
    List<Payment> findByUserAndStatus(User user, PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.status IN :statuses ORDER BY p.createdAt DESC")
    List<Payment> findByUserAndStatusIn(@Param("user") User user, @Param("statuses") List<PaymentStatus> statuses);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user = :user AND p.status = :status")
    BigDecimal getTotalAmountByUserAndStatus(@Param("user") User user, @Param("status") PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findByStatusAndDateRange(@Param("status") PaymentStatus status, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.user = :user AND p.status = :status")
    Long countByUserAndStatus(@Param("user") User user, @Param("status") PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.invoice.id = :invoiceId AND p.status = 'COMPLETED'")
    List<Payment> findSuccessfulPaymentsByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    @Query("SELECT p FROM Payment p WHERE p.status IN ('PENDING', 'PROCESSING') AND p.createdAt < :timeLimit")
    List<Payment> findStalePayments(@Param("timeLimit") LocalDateTime timeLimit);
} 