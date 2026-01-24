package com.hotelCare.hostelCare.dto.employee;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record EmployeeRequestDto(

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number")
        String phoneNumber,

        @Size(max = 500, message = "Profile picture URL too long")
        String profilePic,

        @NotBlank(message = "Job title is required")
        @Size(max = 100, message = "Job title must not exceed 100 characters")
        String jobTitle,

        @NotBlank(message = "Department is required")
        @Size(max = 100, message = "Department must not exceed 100 characters")
        String department,

        @NotNull(message = "Hire date is required")
        LocalDate hireDate,

        LocalDate terminationDate,

        Boolean active,

        LocalDate dateOfBirth,

        @Size(max = 20, message = "Gender must not exceed 20 characters")
        String gender,

        @Size(max = 255, message = "Address too long")
        String address,

        @Size(max = 100, message = "City too long")
        String city,

        @Size(max = 100, message = "Region too long")
        String region,

        @Size(max = 100, message = "Country too long")
        String country,

        @Size(max = 50, message = "Emergency contact name too long")
        String emergencyContactName,

        @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid emergency contact phone number")
        String emergencyContactPhone,

        @NotNull(message = "Salary is required")
        @PositiveOrZero(message = "Salary must be zero or positive")
        Double salary,

        @Size(max = 50, message = "Salary type must not exceed 50 characters")
        String salaryType,

        @Size(max = 100, message = "Bank name too long")
        String bankName,

        @Size(max = 100, message = "Bank account number too long")
        String bankAccountNumber,

        @Size(max = 50, message = "Tax ID too long")
        String taxIdentificationNumber,

        Boolean canAccessSystem,
        Boolean isOnDuty,
        Boolean isVerified
) {}
