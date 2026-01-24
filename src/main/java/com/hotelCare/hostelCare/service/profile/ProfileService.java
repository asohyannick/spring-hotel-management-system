package com.hotelCare.hostelCare.service.profile;
import com.hotelCare.hostelCare.dto.profile.ProfileRequestDto;
import com.hotelCare.hostelCare.dto.profile.ProfileResponseDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
public interface ProfileService {
    ProfileResponseDto createProfile(UUID userId, ProfileRequestDto dto, MultipartFile profilePicture);
    List<ProfileResponseDto> getAllProfiles();
    ProfileResponseDto getProfileById(UUID profileId);
    ProfileResponseDto getProfileByUserId(UUID userId);
    ProfileResponseDto updateProfile(UUID profileId, ProfileRequestDto dto, MultipartFile newProfilePicture);
    void deleteProfile(UUID profileId);
    long totalProfiles();
}
