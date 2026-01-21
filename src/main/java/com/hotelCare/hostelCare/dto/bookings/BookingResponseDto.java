package com.hotelCare.hostelCare.dto.bookings;
import com.hotelCare.hostelCare.enums.BookingStatus;
import com.hotelCare.hostelCare.enums.CancelledBookingStatus;
import com.hotelCare.hostelCare.enums.PaymentMethodStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponseDto(

        UUID id,
        String name,
        String imageUrl,
        String description,
        String region,
        String country,

        LocalDateTime checkInDate,
        LocalDateTime checkOutDate,
        int numberOfNights,
        Integer numberOfGuests,
        Integer numberOfRooms,

        BigDecimal pricePerNight,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,

        BookingStatus status,
        CancelledBookingStatus isCancelled,

        PaymentMethodStatus paymentMethod,
        Boolean isPaid,
        LocalDateTime paymentDate,

        Instant createdAt,
        Instant updatedAt

) {}
