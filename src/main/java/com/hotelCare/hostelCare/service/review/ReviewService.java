package com.hotelCare.hostelCare.service.review;
import com.hotelCare.hostelCare.dto.review.ReviewRequestDto;
import com.hotelCare.hostelCare.dto.review.ReviewResponseDto;
import java.util.List;
import java.util.UUID;

public interface ReviewService {

    ReviewResponseDto createReview(ReviewRequestDto request);

    List<ReviewResponseDto> getAllReviews();

    ReviewResponseDto getReviewById(UUID reviewId);

    ReviewResponseDto updateReview(UUID reviewId, ReviewRequestDto request);

    void deleteReview(UUID reviewId);

    ReviewResponseDto approveReview(UUID reviewId, String moderationNote);

    ReviewResponseDto rejectReview(UUID reviewId, String moderationNote);

    List<ReviewResponseDto> getApprovedReviews();

    List<ReviewResponseDto> getRejectedReviews();

    long countTotalReviews();
}
