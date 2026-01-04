package com.hotelCare.hostelCare.entity.booking;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.BookingStatus;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column
    private String name;

    @JsonIgnore
    @Column
    private String imageUrl;

    @JsonIgnore
    @Column
    private String description;

    @Column
    private BigDecimal price;

    @Column
    private String region;

    @JsonIgnore
    @Column
    private String country;

    @JsonIgnore
    @Column
    private LocalDateTime checkInDate;

    @JsonIgnore
    @Column
    private LocalDateTime checkOutDate;

    @JsonIgnore
    @Column
    private int numberOfNights;

    @JsonIgnore
    @Column
    private Integer numberOfGuests;

    @JsonIgnore
    @Column
    private Integer maxGuests;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

