package com.example.demo.exception;

import com.example.demo.dto.response.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {

        ApiErrorResponse response = new ApiErrorResponse(

                HttpStatus.NOT_FOUND.value(),

                "Not Found",

                exception.getMessage(),

                request.getRequestURI(),

                LocalDateTime.now(),

                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request
    ) {

        ApiErrorResponse response = new ApiErrorResponse(

                HttpStatus.BAD_REQUEST.value(),

                "Bad Request",

                exception.getMessage(),

                request.getRequestURI(),

                LocalDateTime.now(),

                null
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        Map<String, String> validationErrors =
                new HashMap<>();


        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->

                        validationErrors.put(

                                error.getField(),

                                error.getDefaultMessage()
                        )
                );


        ApiErrorResponse response = new ApiErrorResponse(

                HttpStatus.BAD_REQUEST.value(),

                "Validation Failed",

                "One or more request fields are invalid",

                request.getRequestURI(),

                LocalDateTime.now(),

                validationErrors
        );


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception exception,
            HttpServletRequest request
    ) {

        ApiErrorResponse response = new ApiErrorResponse(

                HttpStatus.INTERNAL_SERVER_ERROR.value(),

                "Internal Server Error",

                exception.getMessage(),

                request.getRequestURI(),

                LocalDateTime.now(),

                null
        );


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}