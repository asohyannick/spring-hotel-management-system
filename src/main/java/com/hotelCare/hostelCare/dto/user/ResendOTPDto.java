package com.hotelCare.hostelCare.dto.user;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record ResendOTPDto(
        @NotBlank(message = "Email must be provided")
        @Email(message = "Email is required")
        String email
) { }
