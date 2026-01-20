package com.hotelCare.hostelCare.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordDto(

        @NotBlank(message = "Password must be provided")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
                message = "Password must be at least 8 characters long and contain an uppercase letter, lowercase letter, number, and special character"
        )
        String password,

        @NotBlank(message = "Confirm password must be provided")
        String confirmPassword
) { }
