package com.example.doctor_service.ExceptionHandlers;

import com.example.doctor_service.Exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeException(DateTimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(), "Check input data related to date", LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
