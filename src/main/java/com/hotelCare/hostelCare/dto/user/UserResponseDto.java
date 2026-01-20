package com.hotelCare.hostelCare.dto.user;
import com.hotelCare.hostelCare.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
@Schema(description = "DTO representing a user response")
public record UserResponseDto(
        UUID id,
        @Schema(description = "User's first name", example = "John")
        String firstName,

        @Schema(description = "User's last name", example = "Doe")
        String lastName,

        @Schema(description = "User's email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "User's role in the system", example = "ADMIN")
        String role,

        @Schema(description = "Indicates if the account is blocked")
        @JsonProperty("isAccountBlocked")
        Boolean accountBlocked,

        @Schema(description = "Indicates if the account has been confirmed")
        @JsonProperty("isAccountConfirmed")
        Boolean accountConfirmed,

        @Schema(description = "Indicates if the account is active")
        @JsonProperty("isAccountActive")
        Boolean accountActive,

        @Schema(description = "Indicates if the account is verified")
        @JsonProperty("isAccountVerified")
        Boolean accountVerified,

        @Schema(description = "Current status of the account")
        AccountStatus status
) {}

