package com.hotelCare.hostelCare.entity.user;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column( nullable = false, length = 50)
    private String firstName;

    @Column( nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.CUSTOMER;

    @Column(nullable = false)
    private boolean blocked = false;

    @Column(nullable = false)
    private boolean confirmed = false;

    @Column(nullable = false)
    private boolean active = false;

    @JsonIgnore
    @Column
    private String magicLinkToken;

    @JsonIgnore
    @Column
    private LocalDateTime magicLinkExpiresAt;

    @JsonIgnore
    @Column
    private String otpCode;

    @JsonIgnore
    @Column
    private LocalDateTime otpExpiresAt;

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

