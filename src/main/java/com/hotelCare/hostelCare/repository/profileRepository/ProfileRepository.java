package com.hotelCare.hostelCare.repository.profileRepository;
import com.hotelCare.hostelCare.entity.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUserId(UUID uuid);
    boolean existsByUserId(UUID userId);
}
