package com.hotelCare.hostelCare.exception;

public class ConflictRequestException extends RuntimeException {
    public ConflictRequestException(String message) {
        super(message);
    }

    public ConflictRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictRequestException(Throwable cause) {
        super(cause);
    }
}
