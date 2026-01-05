package com.hotelCare.hostelCare.config.customResponseMessge;

public record CustomResponseMessage<T>(
        String message,
        int statusCode,
        T data
) {}
