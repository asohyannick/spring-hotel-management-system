package com.hotelCare.hostelCare.dto.employee;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
public record EmployeeResponseDto(
        UUID id,

        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String profilePic,

        String jobTitle,
        String department,
        LocalDate hireDate,
        LocalDate terminationDate,
        Boolean active,

        LocalDate dateOfBirth,
        String gender,
        String address,
        String city,
        String region,
        String country,

        String emergencyContactName,
        String emergencyContactPhone,

        Double salary,
        String salaryType,
        String bankName,
        String bankAccountNumber,
        String taxIdentificationNumber,

        Boolean canAccessSystem,
        Boolean isOnDuty,
        Boolean isVerified,

        Instant createdAt,
        Instant updatedAt
) {}
