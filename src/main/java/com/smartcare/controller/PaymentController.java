package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.payment.PaymentIntentResponse;
import com.smartcare.dto.payment.PaymentRequest;
import com.smartcare.dto.payment.PaymentResponse;
import com.smartcare.security.UserPrincipal;
import com.smartcare.service.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Payment processing and management endpoints")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-intent")
    @Operation(summary = "Create payment intent", description = "Create a Stripe payment intent for invoice payment")
    public ResponseEntity<?> createPaymentIntent(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody PaymentRequest request) {
        try {
            PaymentIntentResponse response = paymentService.createPaymentIntent(currentUser.getId(), request);
            return ResponseEntity.ok(new ApiResponse(true, "Payment intent created successfully", response));
        } catch (StripeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Stripe error: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/confirm/{paymentIntentId}")
    @Operation(summary = "Confirm payment", description = "Confirm a payment intent after client-side authentication")
    public ResponseEntity<?> confirmPayment(@PathVariable String paymentIntentId) {
        try {
            PaymentResponse response = paymentService.confirmPayment(paymentIntentId);
            return ResponseEntity.ok(new ApiResponse(true, "Payment confirmed", response));
        } catch (StripeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Stripe error: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/my-payments")
    @Operation(summary = "Get user payments", description = "Retrieve paginated list of user's payments")
    public ResponseEntity<?> getMyPayments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            Pageable pageable) {
        try {
            Page<PaymentResponse> payments = paymentService.getUserPayments(currentUser.getId(), pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Payments retrieved successfully", payments));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieve specific payment details")
    public ResponseEntity<?> getPayment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long paymentId) {
        try {
            PaymentResponse payment = paymentService.getPaymentById(currentUser.getId(), paymentId);
            return ResponseEntity.ok(new ApiResponse(true, "Payment retrieved successfully", payment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/reference/{paymentReference}")
    @Operation(summary = "Get payment by reference", description = "Retrieve payment by reference number")
    public ResponseEntity<?> getPaymentByReference(@PathVariable String paymentReference) {
        try {
            PaymentResponse payment = paymentService.getPaymentByReference(paymentReference);
            return ResponseEntity.ok(new ApiResponse(true, "Payment retrieved successfully", payment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    @Operation(summary = "Stripe webhook", description = "Handle Stripe webhook events")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        // Note: In a production environment, you should verify the webhook signature
        // For this example, we'll skip signature verification
        try {
            // Parse the webhook payload and extract relevant information
            // This is a simplified implementation
            return ResponseEntity.ok(new ApiResponse(true, "Webhook processed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Webhook processing failed"));
        }
    }
} 