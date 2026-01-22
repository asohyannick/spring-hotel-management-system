package com.hotelCare.hostelCare.dto.bookings;
import com.hotelCare.hostelCare.enums.BookingStatus;
import com.hotelCare.hostelCare.enums.CancelledBookingStatus;
import com.hotelCare.hostelCare.enums.PaymentMethodStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingSearchRequestDto(

        BookingStatus status,
        CancelledBookingStatus isCancelled,
        Boolean isPaid,
        PaymentMethodStatus paymentMethod,

        String region,
        String country,

        Double minPrice,
        Double maxPrice,

        LocalDateTime checkInFrom,
        LocalDateTime checkInTo,
        LocalDateTime checkOutFrom,
        LocalDateTime checkOutTo,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,

        UUID userId,
        String bookingReference,

        Integer page,
        Integer size,
        String sortBy,
        String direction

) {}
