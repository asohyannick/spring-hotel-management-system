package com.hotelCare.hostelCare.controller.profile;
import com.hotelCare.hostelCare.config.customResponseMessge.CustomResponseMessage;
import com.hotelCare.hostelCare.dto.profile.ProfileRequestDto;
import com.hotelCare.hostelCare.dto.profile.ProfileResponseDto;
import com.hotelCare.hostelCare.service.profile.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${api.version}/profile")
@Tag(name = "Customer Profile Management Endpoints", description = "The section is responsible to managed the endpoints belong to customer profile.")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(
            summary = "Create profile",
            description = "Create a customer profile for a user. Supports profile picture upload."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping(value = "/create-profile/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponseMessage<ProfileResponseDto>> createProfile(
            @Parameter(description = "User ID")
            @PathVariable UUID userId,

            @Parameter(
                    description = "Profile JSON data",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfileRequestDto.class))
            )
            @RequestPart("data") @Valid ProfileRequestDto dto,

            @Parameter(
                    description = "Profile picture file (jpg/png)",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        ProfileResponseDto created = profileService.createProfile(userId, dto, profilePicture);

        CustomResponseMessage<ProfileResponseDto> body =
                new CustomResponseMessage<>("Profile created successfully", HttpStatus.CREATED.value(), created);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(summary = "Fetch all profiles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profiles fetched successfully")
    })
    @GetMapping("/fetch-profiles")
    public ResponseEntity<CustomResponseMessage<List<ProfileResponseDto>>> getAllProfiles() {
        List<ProfileResponseDto> profiles = profileService.getAllProfiles();

        CustomResponseMessage<List<ProfileResponseDto>> body =
                new CustomResponseMessage<>("Profiles fetched successfully", HttpStatus.OK.value(), profiles);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Fetch profile by profileId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/fetch-profile/{profileId}")
    public ResponseEntity<CustomResponseMessage<ProfileResponseDto>> getProfileById(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId
    ) {
        ProfileResponseDto profile = profileService.getProfileById(profileId);

        CustomResponseMessage<ProfileResponseDto> body =
                new CustomResponseMessage<>("Profile fetched successfully", HttpStatus.OK.value(), profile);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Fetch profile by userId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/fetch-profile-byUserId/{userId}")
    public ResponseEntity<CustomResponseMessage<ProfileResponseDto>> getProfileByUserId(
            @Parameter(description = "User ID") @PathVariable UUID userId
    ) {
        ProfileResponseDto profile = profileService.getProfileByUserId(userId);

        CustomResponseMessage<ProfileResponseDto> body =
                new CustomResponseMessage<>("Profile fetched successfully", HttpStatus.OK.value(), profile);

        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "Update profile",
            description = "Update profile fields and optionally replace profile picture."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PutMapping(value = "/update-profile/{profileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponseMessage<ProfileResponseDto>> updateProfile(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId,

            @Parameter(
                    description = "Profile JSON data",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProfileRequestDto.class))
            )
            @RequestPart("data") @Valid ProfileRequestDto dto,

            @Parameter(
                    description = "New profile picture file (jpg/png)",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) {
        ProfileResponseDto updated = profileService.updateProfile(profileId, dto, profilePicture);

        CustomResponseMessage<ProfileResponseDto> body =
                new CustomResponseMessage<>("Profile updated successfully", HttpStatus.OK.value(), updated);

        return ResponseEntity.ok(body);
    }

    @GetMapping("/total-profiles")
    @Operation(
            summary = "Get total number of profiles",
            description = "Returns the total number of customer profiles in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total profiles fetched successfully")
    })
    public ResponseEntity<CustomResponseMessage<Long>> totalProfiles() {

        long count = profileService.totalProfiles();

        CustomResponseMessage<Long> response =
                new CustomResponseMessage<>(
                        "Total profiles fetched successfully",
                        HttpStatus.OK.value(),
                        count
                );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @DeleteMapping("/delete-profile/{profileId}")
    public ResponseEntity<CustomResponseMessage<Object>> deleteProfile(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId
    ) {
        profileService.deleteProfile(profileId);

        CustomResponseMessage<Object> body =
                new CustomResponseMessage<>("Profile deleted successfully", HttpStatus.OK.value(), null);

        return ResponseEntity.ok(body);
    }
}
