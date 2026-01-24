package com.hotelCare.hostelCare.mappers.reviewMapper;
import com.hotelCare.hostelCare.dto.review.ReviewResponseDto;
import com.hotelCare.hostelCare.entity.review.Review;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class ReviewMapper {

    public ReviewResponseDto toResponseDto(Review review) {
        if (review == null) return null;

        return new ReviewResponseDto(
                review.getId(),
                review.getBooking() != null ? review.getBooking().getId() : null,
                review.getUser() != null ? review.getUser().getId() : null,
                review.getRating(),
                review.getComment(),
                review.getVerified(),
                review.getStatus(),
                review.getHelpfulCount(),
                review.getAnonymous(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    public List<ReviewResponseDto> toResponseDtoList(List<Review> reviews) {
        if (reviews == null) return List.of();
        return reviews.stream().map(this::toResponseDto).toList();
    }
}
