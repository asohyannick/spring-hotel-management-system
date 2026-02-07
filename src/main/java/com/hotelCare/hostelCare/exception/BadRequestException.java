package com.hotelCare.hostelCare.exception;
import lombok.Getter;
@Getter
public class BadRequestException extends RuntimeException {

    private final String errorCode;

    public BadRequestException(String message) {
        super(message);
        this.errorCode = null;
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public BadRequestException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
