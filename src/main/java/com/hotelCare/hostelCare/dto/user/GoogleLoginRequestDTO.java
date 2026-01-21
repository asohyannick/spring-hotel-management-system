package com.hotelCare.hostelCare.dto.user;
import jakarta.validation.constraints.NotBlank;
public record GoogleLoginRequestDTO(
        @NotBlank(message = "Google token cannot be blank")
        String googleToken
) {}