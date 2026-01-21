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

    Optional<User> findByEmail(String email);
    Optional<User> findByTwoFactorCode(String twoFactorCode);
    boolean existsByEmail(String email);
    @Query("""
        SELECT u FROM User u
        WHERE (:role IS NULL OR u.role = :role)
          AND (:active IS NULL OR u.isAccountActive = :active)
          AND (:confirmed IS NULL OR u.isAccountConfirmed = :confirmed)
          AND (:blocked IS NULL OR u.isAccountBlocked = :blocked)
          AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    List<User> searchUsers(
            @Param("role") UserRole role,
            @Param("active") Boolean active,
            @Param("confirmed") Boolean confirmed,
            @Param("blocked") Boolean blocked,
            @Param("email") String email
    );
    Optional<User> findByMagicLinkToken(String token);
}
