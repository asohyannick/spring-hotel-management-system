package com.hotelCare.hostelCare.service.profile;
import com.hotelCare.hostelCare.dto.profile.ProfileRequestDto;
import com.hotelCare.hostelCare.dto.profile.ProfileResponseDto;
import com.hotelCare.hostelCare.entity.profile.Profile;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.exception.BadRequestException;
import com.hotelCare.hostelCare.exception.NotFoundException;
import com.hotelCare.hostelCare.mappers.profileMapper.ProfileMapper;
import com.hotelCare.hostelCare.repository.profileRepository.ProfileRepository;
import com.hotelCare.hostelCare.repository.userRepository.UserRepository;
import com.hotelCare.hostelCare.service.storage.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final FileStorageService fileStorageService;

    @Override
    public ProfileResponseDto createProfile(UUID userId, ProfileRequestDto dto, MultipartFile profilePicture) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (profileRepository.existsByUserId(userId)) {
            throw new BadRequestException("User already has a profile");
        }

        Profile profile = profileMapper.toEntity(dto);
        profile.setUser(user);

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String url = fileStorageService.uploadProfilePicture(profilePicture);
            profile.setProfilePic(url);
        }

        profile.setIsProfileCompleted(true);

        Profile saved = profileRepository.save(profile);
        return profileMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public List<ProfileResponseDto> getAllProfiles() {
        return profileRepository.findAll()
                .stream()
                .map(profileMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProfileResponseDto getProfileById(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found: " + profileId));
        return profileMapper.toResponseDto(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDto getProfileByUserId(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found for user: " + userId));
        return profileMapper.toResponseDto(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfile(UUID profileId, ProfileRequestDto dto, MultipartFile newProfilePicture) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found: " + profileId));

        profileMapper.updateProfileFromDto(dto, profile);

        if (newProfilePicture != null && !newProfilePicture.isEmpty()) {
            String oldUrl = profile.getProfilePic();
            String newUrl = fileStorageService.uploadProfilePicture(newProfilePicture);
            profile.setProfilePic(newUrl);
        }
        profile.setIsProfileCompleted(true);
        return profileMapper.toResponseDto(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public  long totalProfiles() {
        return profileRepository.count();
    }

    @Override
    public void deleteProfile(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found: " + profileId));
        String pic = profile.getProfilePic();
        profileRepository.delete(profile);
    }
}