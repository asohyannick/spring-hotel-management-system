package com.hotelCare.hostelCare.dto.profile;
import com.hotelCare.hostelCare.enums.CountryType;
import com.hotelCare.hostelCare.enums.MaritalStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record ProfileRequestDto(

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Size(max = 500, message = "Profile picture URL too long")
        String profilePic,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Min(value = 0, message = "Age cannot be negative")
        @Max(value = 120, message = "Age seems invalid")
        Integer age,

        @Pattern(
                regexp = "^[+]?[0-9]{7,15}$",
                message = "Invalid phone number"
        )
        String phoneNumber,

        @Pattern(
                regexp = "^[+]?[0-9]{7,15}$",
                message = "Invalid emergency contact number"
        )
        String emergencyContact,

        @Size(max = 255, message = "Address too long")
        String address,

        @Size(max = 100, message = "City name too long")
        String city,

        @Size(max = 100, message = "Region name too long")
        String region,

        @NotNull(message = "Country is required")
        CountryType country,

        @Size(max = 100, message = "Occupation too long")
        String occupation,

        @NotNull(message = "Marital status is required")
        MaritalStatus maritalStatus,

        @Size(max = 500, message = "Special requests too long")
        String specialRequests,

        Boolean newsletterSubscribed

) {}
