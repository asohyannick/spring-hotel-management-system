package com.hotelCare.hostelCare.repository.reviewRepository;
import com.hotelCare.hostelCare.entity.review.Review;
import com.hotelCare.hostelCare.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    boolean existsByBookingId(UUID bookingId);

    List<Review> findAllByStatusAndDeletedFalse(ReviewStatus status);

    List<Review> findAllByDeletedFalse();

    Optional<Review> findByIdAndDeletedFalse(UUID id);

    long countByDeletedFalse();
}
