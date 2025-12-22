package com.codingnomads.demo_web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class handles exceptions globally across all REST controllers.
 * Instead of each controller having its own try-catch blocks, this class intercepts
 * exceptions and returns a consistent response (like 404 Not Found or 500 Internal Server Error).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * General handler for any exception that isn't specifically handled.
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
    }

    /**
     * Specific handler for when a Todo is not found.
     * Returns a 404 status code.
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(TodoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Specific handler for when a Todo List is not found.
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(TodoListNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
