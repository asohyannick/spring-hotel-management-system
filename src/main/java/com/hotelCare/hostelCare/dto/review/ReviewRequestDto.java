package com.hotelCare.hostelCare.dto.review;
import jakarta.validation.constraints.*;
import java.util.UUID;
public record ReviewRequestDto(

        @NotNull(message = "Booking ID is required")
        UUID bookingId,

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must not exceed 5")
        Integer rating,

        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        String comment,

        Boolean anonymous
) {}
