package com.hotelCare.hostelCare.entity.booking;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.BookingStatus;
import com.hotelCare.hostelCare.enums.CancelledBookingStatus;
import com.hotelCare.hostelCare.enums.PaymentMethodStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String imageUrl;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private LocalDateTime checkInDate;

    @Column(nullable = false)
    private LocalDateTime checkOutDate;

    @Column(nullable = false)
    private int numberOfNights;

    @Column(nullable = false)
    private Integer numberOfGuests;

    @Column(nullable = false)
    private Integer numberOfRooms;

    @Column
    private Integer maxGuests;

    @Column(nullable = false)
    private BigDecimal pricePerNight;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column
    private BigDecimal taxAmount;

    @Column
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column
    private String paymentReference;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentMethodStatus paymentMethod = PaymentMethodStatus.CARD;

    @Column
    private Boolean isPaid = false;

    @Column
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column
    private CancelledBookingStatus isCancelled = CancelledBookingStatus.FALSE;

    @Column
    private LocalDateTime cancelledAt;

    @Column(length = 500)
    private String cancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

