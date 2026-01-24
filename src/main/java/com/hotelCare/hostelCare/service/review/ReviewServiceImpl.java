package com.hotelCare.hostelCare.service.review;

import com.hotelCare.hostelCare.dto.review.ReviewRequestDto;
import com.hotelCare.hostelCare.dto.review.ReviewResponseDto;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.entity.review.Review;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.ReviewStatus;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import com.hotelCare.hostelCare.mappers.reviewMapper.ReviewMapper;
import com.hotelCare.hostelCare.repository.bookingRepository.BookingRepository;
import com.hotelCare.hostelCare.repository.reviewRepository.ReviewRepository;
import com.hotelCare.hostelCare.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponseDto createReview(ReviewRequestDto request) {

        if (reviewRepository.existsByBookingId(request.bookingId())) {
            throw new BadRequestException("Review already exists for this booking");
        }

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new NotFoundException("Booking not found: " + request.bookingId()));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.userId()));

        Review review = Review.builder()
                .booking(booking)
                .user(user)
                .rating(request.rating())
                .comment(request.comment())
                .anonymous(Boolean.TRUE.equals(request.anonymous()))
                .verified(true)
                .status(ReviewStatus.PENDING)
                .helpfulCount(0)
                .deleted(false)
                .build();

        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponseDto> getAllReviews() {
        return reviewMapper.toResponseDtoList(
                reviewRepository.findAllByDeletedFalse()
        );
    }

    @Override
    public ReviewResponseDto getReviewById(UUID reviewId) {
        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found: " + reviewId));

        return reviewMapper.toResponseDto(review);
    }


    @Override
    public ReviewResponseDto updateReview(UUID reviewId, ReviewRequestDto request) {

        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found: " + reviewId));

        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new BadRequestException("Only pending reviews can be updated");
        }

        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setAnonymous(Boolean.TRUE.equals(request.anonymous()));

        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(UUID reviewId) {

        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found: " + reviewId));

        review.setDeleted(true);
        reviewRepository.save(review);
    }

    @Override
    public ReviewResponseDto approveReview(UUID reviewId, String moderationNote) {

        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found: " + reviewId));

        review.setStatus(ReviewStatus.APPROVED);
        review.setModerationNote(moderationNote);

        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }

    @Override
    public ReviewResponseDto rejectReview(UUID reviewId, String moderationNote) {

        Review review = reviewRepository.findByIdAndDeletedFalse(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found: " + reviewId));

        review.setStatus(ReviewStatus.REJECTED);
        review.setModerationNote(moderationNote);

        return reviewMapper.toResponseDto(reviewRepository.save(review));
    }


    @Override
    public List<ReviewResponseDto> getApprovedReviews() {
        return reviewMapper.toResponseDtoList(
                reviewRepository.findAllByStatusAndDeletedFalse(ReviewStatus.APPROVED)
        );
    }

    @Override
    public List<ReviewResponseDto> getRejectedReviews() {
        return reviewMapper.toResponseDtoList(
                reviewRepository.findAllByStatusAndDeletedFalse(ReviewStatus.REJECTED)
        );
    }

    @Override
    public long countTotalReviews() {
        return reviewRepository.countByDeletedFalse();
    }
}
