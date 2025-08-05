package com.smartcare.repository;

import com.smartcare.model.PaymentMethod;
import com.smartcare.model.PaymentMethodType;
import com.smartcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    
    List<PaymentMethod> findByUserAndIsActiveTrue(User user);
    
    Optional<PaymentMethod> findByUserAndIsDefaultTrueAndIsActiveTrue(User user);
    
    Optional<PaymentMethod> findByStripePaymentMethodId(String stripePaymentMethodId);
    
    List<PaymentMethod> findByUserAndTypeAndIsActiveTrue(User user, PaymentMethodType type);
    
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.user.id = :userId AND pm.isActive = true ORDER BY pm.isDefault DESC, pm.createdAt DESC")
    List<PaymentMethod> findActivePaymentMethodsByUserIdOrderByDefault(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(pm) FROM PaymentMethod pm WHERE pm.user = :user AND pm.isActive = true")
    Long countActivePaymentMethodsByUser(@Param("user") User user);
    
    boolean existsByUserAndIsDefaultTrueAndIsActiveTrue(User user);
} 