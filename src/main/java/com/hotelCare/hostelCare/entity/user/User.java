package com.hotelCare.hostelCare.entity.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotelCare.hostelCare.enums.AccountStatus;
import com.hotelCare.hostelCare.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @Column( nullable = false, length = 50)
    private String firstName;

    @JsonIgnore
    @Column( nullable = false, length = 50)
    private String lastName;

    @JsonIgnore
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.CUSTOMER;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isAccountBlocked = false;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isAccountConfirmed = false;

    @JsonIgnore
    @Column(nullable = false)
    private Boolean isAccountActive = false;

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

    @JsonIgnore
    @Column
    private Integer twoFactorAttemptsLeft = 0;

    @JsonIgnore
    @Column
    private Integer failedLoginAttempts = 0;

    @JsonIgnore
    @Column
    private LocalDateTime otpExpiresAt;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.PENDING;

    @JsonIgnore
    @Column
    private String accessToken;

    @JsonIgnore
    @Column
    private String refreshToken;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}

