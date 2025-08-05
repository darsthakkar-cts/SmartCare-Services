package com.smartcare.controller;

import com.smartcare.dto.ApiResponse;
import com.smartcare.dto.payment.InvoiceResponse;
import com.smartcare.security.UserPrincipal;
import com.smartcare.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/invoices")
@Tag(name = "Invoices", description = "Invoice management endpoints")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/create-for-appointment/{appointmentId}")
    @Operation(summary = "Create invoice for appointment", description = "Generate invoice for a completed appointment")
    public ResponseEntity<?> createInvoiceForAppointment(@PathVariable Long appointmentId) {
        try {
            InvoiceResponse invoice = invoiceService.createInvoiceForAppointment(appointmentId);
            return ResponseEntity.ok(new ApiResponse(true, "Invoice created successfully", invoice));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/create-custom")
    @Operation(summary = "Create custom invoice", description = "Create a custom invoice for services")
    public ResponseEntity<?> createCustomInvoice(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam String description,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) BigDecimal taxAmount,
            @RequestParam(required = false) BigDecimal discountAmount) {
        try {
            InvoiceResponse invoice = invoiceService.createCustomInvoice(
                    currentUser.getId(), description, amount, taxAmount, discountAmount);
            return ResponseEntity.ok(new ApiResponse(true, "Custom invoice created successfully", invoice));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/my-invoices")
    @Operation(summary = "Get user invoices", description = "Retrieve paginated list of user's invoices")
    public ResponseEntity<?> getMyInvoices(
            @AuthenticationPrincipal UserPrincipal currentUser,
            Pageable pageable) {
        try {
            Page<InvoiceResponse> invoices = invoiceService.getUserInvoices(currentUser.getId(), pageable);
            return ResponseEntity.ok(new ApiResponse(true, "Invoices retrieved successfully", invoices));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{invoiceId}")
    @Operation(summary = "Get invoice by ID", description = "Retrieve specific invoice details with payment history")
    public ResponseEntity<?> getInvoice(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long invoiceId) {
        try {
            InvoiceResponse invoice = invoiceService.getInvoiceById(currentUser.getId(), invoiceId);
            return ResponseEntity.ok(new ApiResponse(true, "Invoice retrieved successfully", invoice));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Get invoice by number", description = "Retrieve invoice by invoice number")
    public ResponseEntity<?> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        try {
            InvoiceResponse invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
            return ResponseEntity.ok(new ApiResponse(true, "Invoice retrieved successfully", invoice));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/{invoiceId}/cancel")
    @Operation(summary = "Cancel invoice", description = "Cancel an unpaid invoice")
    public ResponseEntity<?> cancelInvoice(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long invoiceId,
            @RequestParam(required = false) String reason) {
        try {
            invoiceService.cancelInvoice(currentUser.getId(), invoiceId, reason);
            return ResponseEntity.ok(new ApiResponse(true, "Invoice cancelled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/stats/total-amount")
    @Operation(summary = "Get total amount by status", description = "Get total invoice amount by status")
    public ResponseEntity<?> getTotalAmountByStatus(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam String status) {
        try {
            BigDecimal total = invoiceService.getTotalAmountByUserAndStatus(
                    currentUser.getId(), 
                    com.smartcare.model.InvoiceStatus.valueOf(status.toUpperCase()));
            return ResponseEntity.ok(new ApiResponse(true, "Total amount retrieved", total));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/stats/count")
    @Operation(summary = "Get invoice count by status", description = "Get count of invoices by status")
    public ResponseEntity<?> getInvoiceCountByStatus(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam String status) {
        try {
            Long count = invoiceService.getInvoiceCountByUserAndStatus(
                    currentUser.getId(), 
                    com.smartcare.model.InvoiceStatus.valueOf(status.toUpperCase()));
            return ResponseEntity.ok(new ApiResponse(true, "Invoice count retrieved", count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
} 