package com.hotelCare.hostelCare.dto.review;
import com.hotelCare.hostelCare.enums.ReviewStatus;
import java.time.Instant;
import java.util.UUID;
public record ReviewResponseDto(
        UUID id,

        UUID bookingId,

        UUID userId,

        Integer rating,

        String comment,

        Boolean verified,

        ReviewStatus status,

        Integer helpfulCount,

        Boolean anonymous,

        Instant createdAt,

        Instant updatedAt
) {}
