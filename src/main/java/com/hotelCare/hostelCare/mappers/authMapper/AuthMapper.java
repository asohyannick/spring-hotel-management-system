package com.hotelCare.hostelCare.mappers.authMapper;
import com.hotelCare.hostelCare.dto.user.AuthRequestDto;
import com.hotelCare.hostelCare.dto.user.UserResponseDto;
import com.hotelCare.hostelCare.entity.user.User;
import com.hotelCare.hostelCare.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isAccountBlocked", constant = "false")
    @Mapping(target = "isAccountConfirmed", constant = "false")
    @Mapping(target = "isAccountActive", constant = "false")
    @Mapping(target = "isAccountVerified", constant = "false")
    @Mapping(target = "magicLinkToken", ignore = true)
    @Mapping(target = "magicLinkExpiresAt", ignore = true)
    @Mapping(target = "twoFactorCode", ignore = true)
    @Mapping(target = "twoFactorExpiryTime", ignore = true)
    @Mapping(target = "twoFactorAttemptsLeft", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "otpExpiresAt", ignore = true)
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(AuthRequestDto dto);

    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    @Mapping(target = "accountBlocked", source = "isAccountBlocked")
    @Mapping(target = "accountConfirmed", source = "isAccountConfirmed")
    @Mapping(target = "accountActive", source = "isAccountActive")
    @Mapping(target = "accountVerified", source = "isAccountVerified")
    UserResponseDto toUserResponseDto(User user);

    @Named("roleToString")
    default String mapRoleToString(UserRole role) {
        return role != null ? role.name() : null;
    }
}
