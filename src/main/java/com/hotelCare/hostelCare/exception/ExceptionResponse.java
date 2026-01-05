package com.hotelCare.hostelCare.exception;
import java.time.LocalDateTime;
public record ExceptionResponse(
        LocalDateTime timestamp,
        String message,
        String details,
        int statusCode,
        String errorCode,
        String path,
        String method
) {}
