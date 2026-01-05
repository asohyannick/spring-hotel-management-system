package com.hotelCare.hostelCare.exception;

public class HttpMessageNotReadableException extends RuntimeException {
    public HttpMessageNotReadableException(String message) {
        super(message);
    }
    public HttpMessageNotReadableException(String message, Throwable cause) {
        super(message, cause);
    }
    public  HttpMessageNotReadableException(Throwable cause) {
        super(cause);
    }
}
