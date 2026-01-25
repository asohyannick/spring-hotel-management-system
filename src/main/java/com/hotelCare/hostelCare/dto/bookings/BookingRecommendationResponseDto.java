package com.hotelCare.hostelCare.dto.bookings;
import java.util.List;
public record BookingRecommendationResponseDto(
        BookingResponseDto baseBooking,
        List<BookingResponseDto> recommendedBookings,
        String aiExplanation
) {}
