package com.hotelCare.hostelCare.dto.user;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record ForgotPasswordDto(
        @NotBlank(message = "Email must be provided")
        @Email(message = "Email format is invalid")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                message = "Email must be a valid email address"
        )
        String email
) { }

