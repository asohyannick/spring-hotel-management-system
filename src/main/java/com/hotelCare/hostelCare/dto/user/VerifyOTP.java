package com.hotelCare.hostelCare.dto.user;
import jakarta.validation.constraints.NotBlank;
public record VerifyOTP(
        @NotBlank(message = "Two-factor code is required")
        String twoFactorCode
) {
}
