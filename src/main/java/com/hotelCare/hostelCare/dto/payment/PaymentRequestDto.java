package com.hotelCare.hostelCare.dto.payment;
import com.hotelCare.hostelCare.enums.PaymentCurrency;
import com.hotelCare.hostelCare.enums.ProviderType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
public record PaymentRequestDto(

        @NotNull(message = "Booking ID is required")
        UUID bookingId,

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        PaymentCurrency currency,

        @NotNull(message = "Payment provider is required")
        ProviderType provider,

        @Size(max = 120, message = "Purpose too long")
        String purpose,

        @Size(max = 30, message = "Payment method too long")
        String method

) {}
