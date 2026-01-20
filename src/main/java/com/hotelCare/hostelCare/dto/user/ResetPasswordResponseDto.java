package com.hotelCare.hostelCare.dto.user;
import io.swagger.v3.oas.annotations.media.Schema;
public record ResetPasswordResponseDto(
        @Schema(example = "Password reset successfully")
        String message
) {}
