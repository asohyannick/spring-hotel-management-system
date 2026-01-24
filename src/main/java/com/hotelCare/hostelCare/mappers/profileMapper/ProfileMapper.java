package com.hotelCare.hostelCare.mappers.profileMapper;
import com.hotelCare.hostelCare.dto.profile.ProfileRequestDto;
import com.hotelCare.hostelCare.dto.profile.ProfileResponseDto;
import com.hotelCare.hostelCare.entity.profile.Profile;
import org.mapstruct.*;
@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileResponseDto toResponseDto(Profile profile);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isProfileCompleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Profile toEntity(ProfileRequestDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfileFromDto(ProfileRequestDto dto, @MappingTarget Profile profile);
}
