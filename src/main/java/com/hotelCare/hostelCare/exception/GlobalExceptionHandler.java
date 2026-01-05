package com.hotelCare.hostelCare.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------- 404 NOT FOUND --------------------
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ex.getMessage(),
                "The requested resource could not be found",
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                "Resource not found",
                "The requested resource does not exist",
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return buildResponse(
                errorMessage,
                "The request contains invalid input data",
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                request
        );
    }

    // -------------------- 403 FORBIDDEN --------------------
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ex.getMessage(),
                "Access to this resource is forbidden",
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                request
        );
    }

    // -------------------- 401 UNAUTHORIZED --------------------
    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(
            UnAuthorizedException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ex.getMessage(),
                "Authentication is required to access this resource",
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                request
        );
    }

    // -------------------- 400 BAD REQUEST --------------------
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ex.getMessage(),
                "The request contains invalid data",
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                request
        );
    }

    // -------------------- 400 MALFORMED JSON --------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJsonException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                "Malformed JSON request",
                "Request body is invalid or unreadable",
                HttpStatus.BAD_REQUEST,
                "MALFORMED_JSON",
                request
        );
    }

    // -------------------- 409 CONFLICT --------------------
    @ExceptionHandler(ConflictRequestException.class)
    public ResponseEntity<ExceptionResponse> handleConflictException(
            ConflictRequestException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ex.getMessage(),
                "The request could not be completed due to a conflict",
                HttpStatus.CONFLICT,
                "CONFLICT",
                request
        );
    }

    // -------------------- 500 INTERNAL SERVER ERROR --------------------
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerError(
            InternalServerErrorException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ex.getMessage(),
                "An unexpected error occurred on the server",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                request
        );
    }

    // -------------------- FALLBACK HANDLER --------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                ex.getMessage(),
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "GENERIC_ERROR",
                request
        );
    }

    // -------------------- COMMON RESPONSE BUILDER --------------------
    private ResponseEntity<ExceptionResponse> buildResponse(
            String message,
            String details,
            HttpStatus status,
            String errorCode,
            HttpServletRequest request
    ) {
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                message,
                details,
                status.value(),
                errorCode,
                request.getRequestURI(),
                request.getMethod()
        );

        return ResponseEntity.status(status).body(response);
    }
}
