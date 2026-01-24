package com.hotelCare.hostelCare.dto.payment;

import com.hotelCare.hostelCare.enums.PaymentStatus;
import com.hotelCare.hostelCare.enums.ProviderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponseDto(

        UUID id,

        UUID bookingId,
        UUID userId,

        BigDecimal amount,
        String currency,

        ProviderType provider,
        PaymentStatus status,

        String purpose,
        String method,
        String reference,

        String stripePaymentIntentId,
        String stripeChargeId,


        String paypalOrderId,
        String paypalCaptureId,

        String providerMessage,

        Instant paidAt,
        Instant cancelledAt,
        Instant refundedAt,

        Instant createdAt,
        Instant updatedAt

) {}
