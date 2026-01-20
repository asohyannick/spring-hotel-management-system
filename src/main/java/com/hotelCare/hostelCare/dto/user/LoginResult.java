package com.hotelCare.hostelCare.dto.user;
import com.hotelCare.hostelCare.dto.user.UserResponseDto;
public record LoginResult(
        UserResponseDto user,
        String accessToken,
        String refreshToken
) {}