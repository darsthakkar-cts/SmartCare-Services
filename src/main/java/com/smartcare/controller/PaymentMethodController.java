package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.payment.PaymentMethodRequest;
import com.smartcare.dto.payment.PaymentMethodResponse;
import com.smartcare.security.UserPrincipal;
import com.smartcare.service.PaymentMethodService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
@Tag(name = "Payment Methods", description = "Payment method management endpoints")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @PostMapping("/add")
    @Operation(summary = "Add payment method", description = "Add a new payment method for the user")
    public ResponseEntity<?> addPaymentMethod(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody PaymentMethodRequest request) {
        try {
            PaymentMethodResponse response = paymentMethodService.addPaymentMethod(currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse(true, "Payment method added successfully", response));
        } catch (StripeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Stripe error: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/my-payment-methods")
    @Operation(summary = "Get user payment methods", description = "Retrieve all active payment methods for the user")
    public ResponseEntity<?> getMyPaymentMethods(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            List<PaymentMethodResponse> paymentMethods = paymentMethodService.getUserPaymentMethods(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Payment methods retrieved successfully", paymentMethods));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/default")
    @Operation(summary = "Get default payment method", description = "Retrieve the user's default payment method")
    public ResponseEntity<?> getDefaultPaymentMethod(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            PaymentMethodResponse paymentMethod = paymentMethodService.getDefaultPaymentMethod(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Default payment method retrieved", paymentMethod));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{paymentMethodId}/set-default")
    @Operation(summary = "Set default payment method", description = "Set a payment method as the default")
    public ResponseEntity<?> setDefaultPaymentMethod(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long paymentMethodId) {
        try {
            paymentMethodService.setAsDefaultPaymentMethod(currentUser.getId(), paymentMethodId);
            return ResponseEntity.ok(new ApiResponse(true, "Default payment method updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{paymentMethodId}")
    @Operation(summary = "Remove payment method", description = "Remove a payment method")
    public ResponseEntity<?> removePaymentMethod(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long paymentMethodId) {
        try {
            paymentMethodService.removePaymentMethod(currentUser.getId(), paymentMethodId);
            return ResponseEntity.ok(new ApiResponse(true, "Payment method removed successfully"));
        } catch (StripeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Stripe error: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Get payment method count", description = "Get count of active payment methods")
    public ResponseEntity<?> getPaymentMethodCount(@AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Long count = paymentMethodService.getActivePaymentMethodCount(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Payment method count retrieved", count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
} 