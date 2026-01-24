package com.hotelCare.hostelCare.dto.profile;
import com.hotelCare.hostelCare.enums.CountryType;
import com.hotelCare.hostelCare.enums.MaritalStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
public record ProfileResponseDto(

        UUID id,

        String firstName,
        String lastName,
        String email,
        String profilePic,

        LocalDate dateOfBirth,
        Integer age,

        String phoneNumber,
        String emergencyContact,

        String address,
        String city,
        String region,
        CountryType country,

        String occupation,
        MaritalStatus maritalStatus,

        String specialRequests,
        Boolean newsletterSubscribed,

        Boolean isProfileCompleted,
        Boolean isVerified,

        Instant createdAt,
        Instant updatedAt

) {}
