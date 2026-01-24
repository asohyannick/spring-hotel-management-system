package com.hotelCare.hostelCare.dto.review;
import com.hotelCare.hostelCare.enums.ReviewStatus;
import java.time.Instant;
import java.util.UUID;

public record AdminReviewResponseDto(

        UUID id,

        UUID bookingId,

        UUID userId,

        Integer rating,

        String comment,

        Boolean verified,

        ReviewStatus status,

        String moderationNote,

        Integer helpfulCount,

        Boolean anonymous,

        Boolean deleted,

        Instant createdAt,

        Instant updatedAt
) {}
