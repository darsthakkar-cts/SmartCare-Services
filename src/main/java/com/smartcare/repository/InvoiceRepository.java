package com.smartcare.repository;

import com.smartcare.model.Invoice;
import com.smartcare.model.InvoiceStatus;
import com.smartcare.model.User;
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
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    Page<Invoice> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<Invoice> findByUserAndStatus(User user, InvoiceStatus status);
    
    @Query("SELECT i FROM Invoice i WHERE i.user = :user AND i.status IN :statuses ORDER BY i.createdAt DESC")
    List<Invoice> findByUserAndStatusIn(@Param("user") User user, @Param("statuses") List<InvoiceStatus> statuses);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < :date")
    List<Invoice> findOverdueInvoices(@Param("status") InvoiceStatus status, @Param("date") LocalDateTime date);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.user = :user AND i.status = :status")
    BigDecimal getTotalAmountByUserAndStatus(@Param("user") User user, @Param("status") InvoiceStatus status);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user = :user AND i.status = :status")
    Long countByUserAndStatus(@Param("user") User user, @Param("status") InvoiceStatus status);
    
    @Query("SELECT i FROM Invoice i WHERE i.appointment.id = :appointmentId")
    Optional<Invoice> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT i FROM Invoice i WHERE i.status IN ('PENDING', 'OVERDUE') AND i.dueDate <= :reminderDate")
    List<Invoice> findInvoicesNeedingReminder(@Param("reminderDate") LocalDateTime reminderDate);
} 