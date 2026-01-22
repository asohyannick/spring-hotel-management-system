package com.hotelCare.hostelCare.entity.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotelCare.hostelCare.entity.booking.Booking;
import com.hotelCare.hostelCare.enums.AccountStatus;
import com.hotelCare.hostelCare.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column(nullable = false, length = 50)
    private String firstName;

    @JsonIgnore
    @Column(nullable = false, length = 50)
    private String lastName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.CUSTOMER;


    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.PENDING;

    @JsonIgnore
    @Column(name = "blocked", nullable = false)
    private Boolean isAccountBlocked = false;

    @JsonIgnore
    @Column(name = "active", nullable = false)
    private Boolean isAccountActive = false;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isAccountConfirmed = false;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isAccountVerified = false;

    @JsonIgnore
    @Column
    private String magicLinkToken;

    @JsonIgnore
    @Column
    private LocalDateTime magicLinkExpiresAt;

    @JsonIgnore
    @Column
    private String twoFactorCode;

    @JsonIgnore
    @Column
    private LocalDateTime twoFactorExpiryTime;

    @Builder.Default
    @Column(nullable = false)
    private Integer twoFactorAttemptsLeft = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @JsonIgnore
    @Column
    private LocalDateTime otpExpiresAt;

    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
           cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Booking> bookings = new ArrayList<>();

    @JsonIgnore
    @Column(length = 1000)
    private String accessToken;

    @JsonIgnore
    @Column(length = 1000)
    private String refreshToken;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (isAccountBlocked == null) isAccountBlocked = false;
        if (isAccountActive == null) isAccountActive = false;
        if (isAccountConfirmed == null) isAccountConfirmed = false;
        if (isAccountVerified == null) isAccountVerified = false;
        if (failedLoginAttempts == null) failedLoginAttempts = 0;
        if (twoFactorAttemptsLeft == null) twoFactorAttemptsLeft = 0;
        if (role == null) role = UserRole.CUSTOMER;
        if (status == null) status = AccountStatus.PENDING;
    }
}


