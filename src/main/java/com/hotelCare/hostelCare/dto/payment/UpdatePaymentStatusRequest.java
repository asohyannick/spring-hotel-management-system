package com.hotelCare.hostelCare.dto.payment;
import com.hotelCare.hostelCare.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
public record UpdatePaymentStatusRequest(
        @NotNull PaymentStatus status,
        String providerMessage
) {}
