package com.hotelCare.hostelCare.entity.review;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Table(
        name = "reviews",
        indexes = {
                @Index(name = "idx_reviews_booking_id", columnList = "booking_id"),
                @Index(name = "idx_reviews_user_id", columnList = "user_id"),
                @Index(name = "idx_reviews_status", columnList = "status")
        }
)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private Boolean verified = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(length = 500)
    private String moderationNote;

    @Column(nullable = false)
    private Integer helpfulCount = 0;

    @Column(nullable = false)
    private Boolean anonymous = false;

    @Column(nullable = false)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
