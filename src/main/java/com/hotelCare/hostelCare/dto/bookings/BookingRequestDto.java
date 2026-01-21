package com.hotelCare.hostelCare.dto.bookings;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record BookingRequestDto(

        @NotBlank(message = "Property name is required")
        String name,

        String imageUrl,

        @Size(max = 1000)
        String description,

        @NotBlank(message = "Region is required")
        String region,

        @NotBlank(message = "Country is required")
        String country,

        @NotNull(message = "Check-in date is required")
        LocalDateTime checkInDate,

        @NotNull(message = "Check-out date is required")
        LocalDateTime checkOutDate,

        @Min(value = 1, message = "Number of nights must be at least 1")
        int numberOfNights,

        @Min(value = 1, message = "At least one guest is required")
        Integer numberOfGuests,

        @Min(value = 1, message = "At least one room is required")
        Integer numberOfRooms,

        Integer maxGuests,

        @NotNull(message = "Price per night is required")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal pricePerNight,

        BigDecimal taxAmount,

        BigDecimal discountAmount

) {}
