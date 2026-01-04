package com.hotelCare.hostelCare.repository.userRepository;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    /* =========================
       Basic Lookups
       ========================= */

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    /* =========================
       Account Status Filtering
       ========================= */

    List<User> findByActiveTrue();

    List<User> findByBlockedTrue();

    List<User> findByConfirmedFalse();

    /* =========================
       Flexible / Advanced Search
       ========================= */

    @Query("""
        SELECT u FROM User u
        WHERE (:role IS NULL OR u.role = :role)
          AND (:active IS NULL OR u.active = :active)
          AND (:confirmed IS NULL OR u.confirmed = :confirmed)
          AND (:blocked IS NULL OR u.blocked = :blocked)
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    List<User> searchUsers(
            @Param("role") UserRole role,
            @Param("active") Boolean active,
            @Param("confirmed") Boolean confirmed,
            @Param("blocked") Boolean blocked,
            @Param("email") String email
    );

    /* =========================
       Magic Link / OTP Lookups
       ========================= */

    Optional<User> findByMagicLinkToken(String token);

    Optional<User> findByOtpCode(String otpCode);

    /* =========================
       Admin / Reporting
       ========================= */

    long countByRole(UserRole role);

    long countByActiveTrue();

    long countByBlockedTrue();
}
