package com.hotelCare.hostelCare.dto.bookings;
import com.hotelCare.hostelCare.enums.BookingStatus;
import java.time.LocalDateTime;
import java.math.BigDecimal;
public record BookingUpdateRequestDto(
        LocalDateTime checkInDate,
        LocalDateTime checkOutDate,
        Integer numberOfGuests,
        Integer numberOfRooms,
        BigDecimal pricePerNight,
        BookingStatus status,
        String cancellationReason

) {}
